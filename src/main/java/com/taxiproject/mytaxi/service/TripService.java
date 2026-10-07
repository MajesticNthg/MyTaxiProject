package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.Dto.GeoJsonFeatureDto;
import com.taxiproject.mytaxi.Dto.GpsPointDto;
import com.taxiproject.mytaxi.Dto.TripListDto;
import com.taxiproject.mytaxi.Dto.TripResponseDto;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.entity.GpsPoint;
import com.taxiproject.mytaxi.entity.Trip;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.GpsPointRepository;
import com.taxiproject.mytaxi.repository.TripRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import com.taxiproject.mytaxi.service.GeoapifyService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final GpsPointRepository gpsPointRepository;
    private final CarRepository carRepository;
    private final GeoapifyService geoapifyService;

    public TripService(TripRepository tripRepository, GpsPointRepository gpsPointRepository, CarRepository carRepository, GeoapifyService geoapifyService) {
        this.tripRepository = tripRepository;
        this.gpsPointRepository = gpsPointRepository;
        this.carRepository = carRepository;
        this.geoapifyService = geoapifyService;
    }

    public List<TripResponseDto> getTripWithPoints (Integer carId, LocalDateTime from, LocalDateTime to) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("машина не найдена"));

        Enterprise enterprise = car.getEnterprise();
        ZoneId zone = ZoneId.of(enterprise.getTimezone() != null ? enterprise.getTimezone() : "UTC");

        List<Trip> trips = tripRepository.findByCarAndPeriod(carId, from, to);

        List<TripResponseDto> result = new ArrayList<>();

        for (Trip trip : trips) {
            List<GpsPoint> points = gpsPointRepository.findByCarIdAndTimestampBetweenOrderByTimestampAsc(carId, trip.getStartTime(), trip.getEndTime());

            TripResponseDto dto = new TripResponseDto();
            dto.setTripId(trip.getId());
            dto.setCarId(car.getId());
            dto.setStartTime(formatInZone(trip.getStartTime(), zone));
            dto.setEndTime(formatInZone(trip.getEndTime(), zone));
            dto.setPoints(points.stream().map(p -> convertToDto(p, zone, trip.getId())).collect(Collectors.toList()));

            result.add(dto);
        }

        return result;
    }

    public List<GpsPointDto> getTripAsSingleTrack(Integer carId, LocalDateTime from, LocalDateTime to) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("машина не найдена"));

        Enterprise enterprise = car.getEnterprise();
        ZoneId zone = ZoneId.of(enterprise.getTimezone() != null ? enterprise.getTimezone() : "UTC");
        List<Trip> trips = tripRepository.findByCarAndPeriod(carId, from, to);

        List<GpsPointDto> result = new ArrayList<>();
        for (Trip trip : trips) {
            List<GpsPoint> points = gpsPointRepository.findByCarIdAndTimestampBetweenOrderByTimestampAsc(carId, trip.getStartTime(), trip.getEndTime());
            tripIdOnDTO(points, zone, trip);
            result.addAll(tripIdOnDTO(points, zone, trip));
        }

        return result;
    }

    private List<GpsPointDto> tripIdOnDTO (List<GpsPoint> points, ZoneId zone, Trip trip) {
        List<GpsPointDto> allPoints = new ArrayList<>();
        for (GpsPoint point : points) {
            allPoints.add(convertToDto(point, zone, trip.getId()));
        }
        return allPoints;
    }

    private String formatInZone (LocalDateTime utc, ZoneId zone) {
        return utc.atZone(ZoneId.of("UTC")).withZoneSameInstant(zone).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private GpsPointDto convertToDto(GpsPoint point, ZoneId zone, Long tripId) {
        ZonedDateTime zonedTime = point.getTimestamp().atZone(ZoneId.of("UTC")).withZoneSameInstant(zone);
        return new GpsPointDto(
                point.getId(),
                tripId,
                point.getLatitude(),
                point.getLongitude(),
                point.getAltitude(),
                point.getSpeed(),
                point.getHeading(),
                zonedTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    public List<TripListDto> getTripList(Integer carId, LocalDateTime from, LocalDateTime to) {
        System.out.println("🔍 Запрос поездок: carId=" + carId + ", from=" + from + ", to=" + to);
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Машина не найдена"));

        Enterprise enterprise = car.getEnterprise();
        ZoneId zone = ZoneId.of(enterprise.getTimezone() != null ? enterprise.getTimezone() : "UTC");

        List<Trip> trips = tripRepository.findByCarAndPeriod(carId, from, to);
        System.out.println("📋 Найдено поездок в репозитории: " + trips.size());
        List<TripListDto> result = new ArrayList<>();

        List<double[]> coordinates = new ArrayList<>();
        List<int[]> tripIndexes = new ArrayList<>();

        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);

            List<GpsPoint> points = gpsPointRepository.findByCarIdAndTimestampBetweenOrderByTimestampAsc(carId, trip.getStartTime(), trip.getEndTime());

            if (!points.isEmpty()) {
                GpsPoint startPoint = points.get(0);
                GpsPoint endPoint = points.get(points.size() - 1);

                coordinates.add(new double[]{startPoint.getLatitude(), startPoint.getLongitude()});
                tripIndexes.add(new int[]{i, 0});

                coordinates.add(new double[]{endPoint.getLatitude(), endPoint.getLongitude()});
                tripIndexes.add(new int[]{i, 1});
            }
        }

        Map<String, String> addresses = geoapifyService.reverseGeocodeBatch(coordinates);

        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);

            List<GpsPoint> points = gpsPointRepository.findByCarIdAndTimestampBetweenOrderByTimestampAsc(carId, trip.getStartTime(), trip.getEndTime());

            if (points.isEmpty()) {
                continue;
            }

            GpsPoint startPoint = points.get(0);
            GpsPoint endPoint = points.get(points.size() - 1);

            TripListDto dto = new TripListDto();
            dto.setTripId(trip.getId());
            dto.setCarId(car.getId());
            dto.setStartTime(formatInZone(trip.getStartTime(), zone));
            dto.setEndTime(formatInZone(trip.getEndTime(), zone));
            dto.setStartLat(startPoint.getLatitude());
            dto.setStartLon(startPoint.getLongitude());
            dto.setStartAddress(addresses.getOrDefault(String.valueOf(i * 2), "Адрес не найден"));
            dto.setEndLat(endPoint.getLatitude());
            dto.setEndLon(endPoint.getLongitude());
            dto.setEndAddress(addresses.getOrDefault(String.valueOf(i * 2 + 1), "Адрес не найден"));

            result.add(dto);
        }

        return result;
    }
}
