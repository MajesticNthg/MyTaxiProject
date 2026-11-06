package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.Dto.ReportRequestDto;
import com.taxiproject.mytaxi.Dto.ReportResponseDto;
import com.taxiproject.mytaxi.entity.*;
import com.taxiproject.mytaxi.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final CarRepository carRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final TripRepository tripRepository;
    private final GpsPointRepository gpsPointRepository;

    public ReportService(CarRepository carRepository,
                         EnterpriseRepository enterpriseRepository,
                         TripRepository tripRepository,
                         GpsPointRepository gpsPointRepository) {
        this.carRepository = carRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.tripRepository = tripRepository;
        this.gpsPointRepository = gpsPointRepository;
    }

    public ReportResponseDto generateReport(ReportRequestDto request) {
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Машина не найдена"));
        Enterprise enterprise = enterpriseRepository.findById(request.getEnterpriseId())
                .orElseThrow(() -> new RuntimeException("Предприятие не найдено"));

        // ✅ Получаем поездки за период
        LocalDateTime from = request.getStartDate().atStartOfDay();
        LocalDateTime to = request.getEndDate().atTime(23, 59, 59);

        List<Trip> trips = tripRepository.findByCarAndPeriod(
                car.getId(), from, to
        );

        // ✅ Группируем по периоду
        Map<String, Double> result;
        if ("MILEAGE".equals(request.getReportType())) {
            result = calculateMileage(trips, request.getPeriodType());
        } else if ("TRAVEL_TIME".equals(request.getReportType())) {
            result = calculateTravelTime(trips, request.getPeriodType());
        } else {
            throw new RuntimeException("Неизвестный тип отчёта: " + request.getReportType());
        }

        // ✅ Формируем ответ
        ReportResponseDto response = new ReportResponseDto();
        response.setReportType(request.getReportType());
        response.setName(getReportName(request.getReportType()));
        response.setCarId(car.getId());
        response.setCarLicensePlate(car.getLicensePlate());
        response.setEnterpriseId(enterprise.getId());
        response.setEnterpriseName(enterprise.getName());
        response.setStartDate(request.getStartDate());
        response.setEndDate(request.getEndDate());
        response.setPeriodType(request.getPeriodType());

        List<ReportResponseDto.ReportItemDto> items = new ArrayList<>();
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            ReportResponseDto.ReportItemDto item = new ReportResponseDto.ReportItemDto();
            item.setPeriod(entry.getKey());
            item.setValue(entry.getValue());
            item.setUnit("MILEAGE".equals(request.getReportType()) ? "км" : "мин");
            items.add(item);
        }
        response.setItems(items);

        return response;
    }

    // ✅ Пробег
    private Map<String, Double> calculateMileage(List<Trip> trips, String periodType) {
        Map<String, Double> result = new TreeMap<>();

        for (Trip trip : trips) {
            // Пробег = сумма расстояний между GPS-точками
            List<GpsPoint> points = gpsPointRepository
                    .findByCarIdAndTimestampBetweenOrderByTimestampAsc(
                            trip.getCar().getId(), trip.getStartTime(), trip.getEndTime()
                    );

            double mileage = calculateDistance(points);
            String period = formatPeriod(trip.getStartTime(), periodType);

            result.merge(period, mileage, Double::sum);
        }

        return result;
    }

    // ✅ Время в пути
    private Map<String, Double> calculateTravelTime(List<Trip> trips, String periodType) {
        Map<String, Double> result = new TreeMap<>();

        for (Trip trip : trips) {
            // Время = разница между startTime и endTime (в минутах)
            long minutes = java.time.Duration.between(trip.getStartTime(), trip.getEndTime()).toMinutes();
            String period = formatPeriod(trip.getStartTime(), periodType);

            result.merge(period, (double) minutes, Double::sum);
        }

        return result;
    }

    // ✅ Форматирование периода
    private String formatPeriod(LocalDateTime dateTime, String periodType) {
        return switch (periodType) {
            case "DAY" -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case "MONTH" -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            case "YEAR" -> dateTime.format(DateTimeFormatter.ofPattern("yyyy"));
            default -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        };
    }

    // ✅ Расстояние между GPS-точками (формула Haversine)
    private double calculateDistance(List<GpsPoint> points) {
        if (points.size() < 2) return 0.0;

        double total = 0.0;
        for (int i = 1; i < points.size(); i++) {
            GpsPoint prev = points.get(i - 1);
            GpsPoint curr = points.get(i);
            total += haversine(prev.getLatitude(), prev.getLongitude(),
                    curr.getLatitude(), curr.getLongitude());
        }
        return total;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // радиус Земли в км
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String getReportName(String reportType) {
        return switch (reportType) {
            case "MILEAGE" -> "Пробег автомобиля";
            case "TRAVEL_TIME" -> "Время в пути";
            default -> "Отчёт";
        };
    }
}
