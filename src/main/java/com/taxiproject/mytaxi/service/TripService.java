package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.Dto.GeoJsonFeatureDto;
import com.taxiproject.mytaxi.Dto.GpsPointDto;
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

    public TripService(TripRepository tripRepository, GpsPointRepository gpsPointRepository, CarRepository carRepository) {
        this.tripRepository = tripRepository;
        this.gpsPointRepository = gpsPointRepository;
        this.carRepository = carRepository;
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



//        List<List<Double>> coordinates = allPoints.stream().map(p -> List.of(p.getLongitude(), p.getLatitude())).collect(Collectors.toList());
//
//        Map<String, Object> geometry = new HashMap<>();
//        geometry.put("type", "LineString");
//        geometry.put("coordinates", coordinates);
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("carId", carId);
//        properties.put("tripCount", trips.size());
//        properties.put("pointCount", allPoints.size());
//
//        if (!trips.isEmpty()) {
//            ZonedDateTime fromZoned = trips.get(0).getStartTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(zone);
//            ZonedDateTime toZoned = trips.get(trips.size() - 1).getEndTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(zone);
//
//            properties.put("from", fromZoned.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            properties.put("to", toZoned.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        }
//
//        Map<String, Object> feature = new HashMap<>();
//        feature.put("type", "Feature");
//        feature.put("geometry", geometry);
//        feature.put("properties", properties);


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
}
