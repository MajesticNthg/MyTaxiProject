package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.Dto.GpsPointDto;
import com.taxiproject.mytaxi.Dto.GeoJsonFeatureDto;
import com.taxiproject.mytaxi.Dto.GeoJsonGeometry;
import com.taxiproject.mytaxi.Dto.GeoJsonProperties;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.entity.GpsPoint;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.repository.GpsPointRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GpsService {

    private final GpsPointRepository gpsPointRepository;
    private final CarRepository carRepository;
    private final EnterpriseRepository enterpriseRepository;

    public GpsService(GpsPointRepository gpsPointRepository, CarRepository carRepository, EnterpriseRepository enterpriseRepository) {
        this.gpsPointRepository = gpsPointRepository;
        this.carRepository = carRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    public List<GpsPointDto> getPointsAsJson(UUID carId, LocalDateTime from, LocalDateTime to, String enterpriseTimezone) {
        List<GpsPoint> points = gpsPointRepository.findByCarIdAndTimestampBetweenOrderByTimestampAsc(carId, from, to);

        ZoneId enterpriseZone = ZoneId.of(enterpriseTimezone != null ? enterpriseTimezone : "UTC");

        return points.stream().map(p -> convertToDto(p, enterpriseZone, null)).collect(Collectors.toList());
    }

    public Object getPointsAsGeoJson(UUID carId, LocalDateTime from, LocalDateTime to, String enterpriseTimezone, boolean asTrack) {
        List<GpsPoint> points = gpsPointRepository.findByCarIdAndTimestampBetweenOrderByTimestampAsc(carId, from, to);

        ZoneId enterpriseZone = ZoneId.of(enterpriseTimezone != null ? enterpriseTimezone : "UTC");

        if (asTrack) {
            return buildGeoJsonLineString(points, enterpriseZone);
        } else {
            return buildGeoJsonFeatureCollection(points, enterpriseZone);
        }
    }

    private GpsPointDto convertToDto(GpsPoint point, ZoneId zone, UUID tripId) {
        ZonedDateTime zonedTime = point.getTimestamp().atZone(ZoneId.of("UTC")).withZoneSameInstant(zone);
        return new GpsPointDto(
                point.getId(),
                tripId,
                point.getLatitude(),
                point.getLongitude(),
                point.getAltitude(),
                point.getSpeed(),
                point.getHeading(),
                zonedTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private Map<String, Object> buildGeoJsonFeatureCollection(List<GpsPoint> points, ZoneId zone) {
        List<Map<String, Object>> features = new ArrayList<>();

        for (GpsPoint point : points) {
            Map<String, Object> geometry = new HashMap<>();
            geometry.put("type", "Point");
            geometry.put("coordinates", List.of(point.getLongitude(), point.getLatitude()));

            Map<String, Object> properties = new HashMap<>();
            properties.put("id", point.getId());
            properties.put("speed", point.getSpeed());
            properties.put("altitude", point.getAltitude());
            properties.put("heading", point.getHeading());
            properties.put("accuracy", point.getAccuracy());

            ZonedDateTime zonedTime = point.getTimestamp().atZone(ZoneId.of("UTC")).withZoneSameInstant(zone);
            properties.put("timestamp", zonedTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            Map<String, Object> feature = new HashMap<>();
            feature.put("type", "Feature");
            feature.put("geometry", geometry);
            feature.put("properties", properties);

            features.add(feature);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("type", "FeatureCollection");
        result.put("features", features);

        return result;
    }

    private Map<String, Object> buildGeoJsonLineString(List<GpsPoint> points, ZoneId zone) {
        List<List<Double>> coordinates = points.stream()
                .map(p -> List.of(p.getLongitude(), p.getLatitude()))
                .collect(Collectors.toList());

        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "LineString");
        geometry.put("coordinates", coordinates);

        Map<String, Object> properties = new HashMap<>();
        properties.put("pointCount", points.size());

        if (!points.isEmpty()) {
            Car car = points.get(0).getCar();
            properties.put("carId", car.getId());
            properties.put("carLicensePlate", car.getLicensePlate());

            ZonedDateTime from = points.get(0).getTimestamp().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(zone);
            ZonedDateTime to = points.get(points.size() - 1).getTimestamp().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(zone);

            properties.put("from", from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            properties.put("to", to.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", properties);

        return feature;
    }
}
