package com.taxiproject.mytaxi.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.taxiproject.mytaxi.Dto.*;
import com.taxiproject.mytaxi.entity.*;
import com.taxiproject.mytaxi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByName;
import java.lang.reflect.Field;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByName;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Field;
import java.io.Writer;
import java.util.List;

@Service
public class ExportImportService {
    private final EnterpriseRepository enterpriseRepository;
    private final CarRepository carRepository;
    private final TripRepository tripRepository;
    private final GpsPointRepository gpsPointRepository;
    private final BrandRepository brandRepository;
    private final GeoapifyService geoapifyService;


    public ExportImportService(EnterpriseRepository enterpriseRepository,
                               CarRepository carRepository,
                               TripRepository tripRepository,
                               GpsPointRepository gpsPointRepository,
                               BrandRepository brandRepository,
                               GeoapifyService geoapifyService) {
        this.enterpriseRepository = enterpriseRepository;
        this.carRepository = carRepository;
        this.tripRepository = tripRepository;
        this.gpsPointRepository = gpsPointRepository;
        this.brandRepository = brandRepository;
        this.geoapifyService = geoapifyService;
    }

    public List<EnterpriseExportDto> exportEnterprises() {
        return enterpriseRepository.findAll().stream()
                .map(this::toEnterpriseDto)
                .collect(Collectors.toList());
    }

    public List<CarExportDto> exportCars(UUID enterpriseId) {
        return carRepository.findByEnterpriseId(enterpriseId).stream()
                .map(this::toCarDto)
                .collect(Collectors.toList());
    }

    public List<TripExportDto> exportTrips(UUID carId, LocalDateTime from, LocalDateTime to) {
        List<Trip> trips = tripRepository.findByCarAndPeriod(carId, from, to);
        List<TripExportDto> result = new ArrayList<>();

        for (Trip trip : trips) {
            TripExportDto dto = toTripDto(trip);
            result.add(dto);
        }
        return result;
    }

    public void writeCsv(List<?> data, Writer writer) throws Exception {
        if (data.isEmpty()) {
            return;
        }

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            Field[] fields = data.get(0).getClass().getDeclaredFields();
            String[] headers = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                CsvBindByName annotation = fields[i].getAnnotation(CsvBindByName.class);
                headers[i] = annotation != null ? annotation.column() : fields[i].getName();
            }
            csvWriter.writeNext(headers);

            for (Object item : data) {
                String[] row = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(item);
                    row[i] = value != null ? value.toString() : "";
                }
                csvWriter.writeNext(row);
            }
        }
    }

    @Transactional
    public int importEnterprises(MultipartFile file) throws Exception {
        List<EnterpriseExportDto> dtos = parseCsv(file, EnterpriseExportDto.class);
        int count = 0;

        for (EnterpriseExportDto dto : dtos) {
            if (enterpriseRepository.findByName(dto.getName()).isPresent()) {
                System.out.println("Пропущено (уже существует): " + dto.getName());
                continue;
            }

            Enterprise enterprise = new Enterprise();
            enterprise.setName(dto.getName());
            enterprise.setCity(dto.getCity());
            enterprise.setEmployeeCount(dto.getEmployeeCount());
            enterprise.setTimezone(dto.getTimezone());
            enterpriseRepository.save(enterprise);
            count++;
        }
        return count;
    }

    @Transactional
    public int importCars(MultipartFile file, UUID enterpriseId) throws Exception {
        List<CarExportDto> dtos = parseCsv(file, CarExportDto.class);
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Предприятие не найдено"));
        int count = 0;

        for (CarExportDto dto : dtos) {
            if (carRepository.existsByLicensePlate(dto.getLicensePlate())) {
                System.out.println("Пропущено (уже существует): " + dto.getLicensePlate());
                continue;
            }

            Car car = new Car();
            car.setLicensePlate(dto.getLicensePlate());
            car.setYearOfRelease(dto.getYearOfRelease());
            car.setCarMileage(dto.getCarMileage());
            car.setPrice(dto.getPrice());
            car.setEnterprise(enterprise);

            if (dto.getBrandName() != null) {
                Brand brand = brandRepository.findByBrandName(dto.getBrandName())
                        .orElse(null);
                car.setBrand(brand);
            }
            carRepository.save(car);
            count++;
        }
        return count;
    }

    @Transactional
    public int importTrips(MultipartFile file, UUID carId, String filterStartAddress) throws Exception {
        List<TripExportDto> dtos = parseCsv(file, TripExportDto.class);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Машина не найдена: " + carId));

        int count = 0;

        for (TripExportDto dto : dtos) {
            Trip trip = new Trip();
            trip.setCar(car);
            trip.setStartTime(LocalDateTime.parse(dto.getStartTime()));
            trip.setEndTime(LocalDateTime.parse(dto.getEndTime()));
            trip.setCreatedAt(LocalDateTime.now());

            if (dto.getStartAddress() != null && !dto.getStartAddress().isEmpty()) {
                double[] coords = geoapifyService.geocodeAddress(dto.getStartAddress());
                if (coords != null) {
                    trip.setStartAddress(dto.getStartAddress());
                } else {
                    System.err.println("Не удалось геокодировать: " + dto.getStartAddress());
                    continue;
                }
            }

            if (dto.getEndAddress() != null && !dto.getEndAddress().isEmpty()) {
                double[] coords = geoapifyService.geocodeAddress(dto.getEndAddress());
                if (coords != null) {
                    trip.setEndAddress(dto.getEndAddress());
                } else {
                    System.err.println("Не удалось геокодировать: " + dto.getEndAddress());
                    continue;
                }
            }

            tripRepository.save(trip);
            count++;
        }

        System.out.println("✅ Импортировано поездок: " + count);
        return count;
    }

    private <T> List<T> parseCsv(MultipartFile file, Class<T> clazz) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }

    private EnterpriseExportDto toEnterpriseDto(Enterprise e) {
        EnterpriseExportDto dto = new EnterpriseExportDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setCity(e.getCity());
        dto.setEmployeeCount(e.getEmployeeCount());
        dto.setTimezone(e.getTimezone());
        return dto;
    }

    private CarExportDto toCarDto(Car car) {
        CarExportDto dto = new CarExportDto();
        dto.setId(car.getId());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setYearOfRelease(car.getYearOfRelease());
        dto.setCarMileage(car.getCarMileage());
        dto.setPrice(car.getPrice());
        dto.setEnterpriseId(car.getEnterprise() != null ? car.getEnterprise().getId() : null);
        if (car.getBrand() != null) {
            dto.setBrandName(car.getBrand().getBrandName());
        }
        return dto;
    }

    private TripExportDto toTripDto(Trip trip) {
        TripExportDto dto = new TripExportDto();
        dto.setId(trip.getId());
        dto.setCarId(trip.getCar().getId());
        dto.setStartTime(trip.getStartTime().toString());
        dto.setEndTime(trip.getEndTime().toString());
        dto.setStartAddress(trip.getStartAddress());
        dto.setEndAddress(trip.getEndAddress());

        List<GpsPoint> points = gpsPointRepository
                .findByCarIdAndTimestampBetweenOrderByTimestampAsc(
                        trip.getCar().getId(), trip.getStartTime(), trip.getEndTime()
                );

        if (!points.isEmpty()) {
            GpsPoint start = points.get(0);
            GpsPoint end = points.get(points.size() - 1);
            dto.setStartLat(start.getLatitude());
            dto.setStartLon(start.getLongitude());
            dto.setEndLat(end.getLatitude());
            dto.setEndLon(end.getLongitude());
        }
        return dto;
    }

    @Transactional
    public Trip importSingleTripByAddress(TripSingleImportDto dto, UUID carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Машина не найдена: " + carId));

        Trip trip = new Trip();
        trip.setCar(car);
        trip.setStartTime(LocalDateTime.parse(dto.getStartTime()));
        trip.setEndTime(LocalDateTime.parse(dto.getEndTime()));
        trip.setCreatedAt(LocalDateTime.now());

        if (dto.getStartAddress() != null && !dto.getStartAddress().isEmpty()) {
            double[] coords = geoapifyService.geocodeAddress(dto.getStartAddress());
            if (coords != null) {
                trip.setStartAddress(dto.getStartAddress());
                System.out.println("✅ Старт: " + dto.getStartAddress() + " → [" + coords[0] + ", " + coords[1] + "]");
            } else {
                throw new RuntimeException("Не удалось геокодировать адрес старта: " + dto.getStartAddress());
            }
        }

        if (dto.getEndAddress() != null && !dto.getEndAddress().isEmpty()) {
            double[] coords = geoapifyService.geocodeAddress(dto.getEndAddress());
            if (coords != null) {
                trip.setEndAddress(dto.getEndAddress());
                System.out.println("Финиш: " + dto.getEndAddress() + " → [" + coords[0] + ", " + coords[1] + "]");
            } else {
                throw new RuntimeException("Не удалось геокодировать адрес финиша: " + dto.getEndAddress());
            }
        }

        return tripRepository.save(trip);
    }
}
