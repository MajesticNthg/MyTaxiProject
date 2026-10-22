package com.taxiproject.mytaxi.service;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.reader.osm.OSMReader;
import com.graphhopper.util.PointList;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.GpsPoint;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.GpsPointRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.graphhopper.config.Profile;
import com.graphhopper.config.CHProfile;
import com.graphhopper.json.Statement;
import com.graphhopper.config.Profile;
import com.graphhopper.config.CHProfile;
import com.graphhopper.util.CustomModel;
import com.graphhopper.json.Statement;
import com.graphhopper.util.CustomModel;
import com.graphhopper.json.Statement;


import java.io.File;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GpsTrackGeneratorService {

    private final CarRepository carRepository;
    private final GpsPointRepository gpsPointRepository;
    private GraphHopper graphHopper;

    @Value("${graphhopper.osm.file:src/main/resources/maps/russia-latest.osm.pbf}")
    private String osmFilePath;

    @Value("${graphhopper.graph.location:src/maim/resources/maps/graph-cache}")
    private String graphLocation;

    private final Random random = new Random();

    public GpsTrackGeneratorService(CarRepository carRepository, GpsPointRepository gpsPointRepository) {
        this.carRepository = carRepository;
        this.gpsPointRepository = gpsPointRepository;
    }

    @PostConstruct
    public void initGraphHopper() {
        try {
            File osmFile = new File(osmFilePath);
            if (!osmFile.exists()) {
                System.err.println("OSM файл не найден: " + osmFilePath);
                System.err.println("GraphHopper инициализирован без карты. Генерация треков по дорогам недоступна");
                return;
            }

            System.out.println("Инициализация GraphHopper с картой: " + osmFilePath);

            graphHopper = new GraphHopper();
            graphHopper.setOSMFile(osmFilePath);
            graphHopper.setGraphHopperLocation(graphLocation);
            graphHopper.setEncodedValuesString("car_access, car_average_speed, road_access");
            CustomModel customModel = new CustomModel();
            customModel.addToSpeed(Statement.If("true", Statement.Op.LIMIT, "100"));
            customModel.addToPriority(Statement.If("road_access == DESTINATION", Statement.Op.MULTIPLY, "0.5"));
            Profile carProfile = new Profile("car").setCustomModel(customModel);

            graphHopper.setProfiles(carProfile);
            graphHopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
            graphHopper.importOrLoad();
            System.out.println("GraphHopper инициализирован успешно");
        } catch (Exception e) {
            System.out.println("Ошибка инициализации GraphHopper: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroyGraphHopper() {
        if (graphHopper != null) {
            graphHopper.close();
            System.out.println("GraphHopper закрыт");
        }
    }

    public List<GpsPoint> generateTrackByRoads(UUID carId, double startLat, double startLon, double endLat, double endLon,
                                               int pointsCount, int intervalSeconds, double speedMin, double speedMax, double altitudeMin, double altitudeMax) throws Exception {
        if (graphHopper == null) {
            throw new IllegalStateException("GraphHopper не инициализирован. Проверьта наличие OSM файла");
        }

        Car car = carRepository.findById(carId).orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));

        GHRequest request = new GHRequest(startLat, startLon, endLat, endLon).setProfile("car");
        GHResponse response = graphHopper.route(request);

        if (response.hasErrors()) {
            throw new RuntimeException("Ошибка построения маршрута: " + response.getErrors());
        }

        PointList routePoints = response.getBest().getPoints();
        System.out.println("Маршрут построен. Всего точек: " + routePoints.size());

        List<GpsPoint> track = new ArrayList<>();
        double totalDistance=  response.getBest().getDistance();
        int step = Math.max(1, routePoints.size() / pointsCount);

        LocalDateTime now = LocalDateTime.now().withNano(0);
        ZoneId utc = ZoneId.of("UTC");

        for (int i = 0; i < pointsCount && i * step < routePoints.size(); i++) {
            int idx = Math.min(i + step, routePoints.size() - 1);
            double lat = routePoints.getLat(idx);
            double lon = routePoints.getLon(idx);

            double noise = 0.00005;
            lat += ThreadLocalRandom.current().nextDouble(-noise, noise);
            lon += ThreadLocalRandom.current().nextDouble(-noise, noise);

            double progress = (double) i / pointsCount;
            double speedFactor = 1.0 - 0.5 * Math.pow((progress - 0.5) * 2, 2);
            double speed = (speedMin + (speedMax - speedMin) * speedFactor) + ThreadLocalRandom.current().nextDouble(-5, 5);

            double altProgress = (double) i / pointsCount;
            double altitude = altitudeMin + (altitudeMax - altitudeMin) * altProgress + ThreadLocalRandom.current().nextDouble(-10, 10);

            double heading = 0;
            int nextIdx = Math.min(idx + 1, routePoints.size() -1 );
            if (idx < routePoints.size() - 1) {
                double dLat = routePoints.getLat(nextIdx) - lat;
                double dLon = routePoints.getLon(nextIdx) - lon;
                heading = Math.toDegrees(Math.atan2(dLon, dLat));
                if (heading < 0) {
                    heading += 360;
                }
            }

            LocalDateTime timestamp = now.plusSeconds(i * intervalSeconds);

            GpsPoint point = new GpsPoint();
            point.setCar(car);
            point.setLatitude(lat);
            point.setLongitude(lon);
            point.setAltitude(Math.round(altitude * 10) / 10.0);
            point.setSpeed(Math.round(speed * 10) / 10.0);
            point.setHeading(Math.round(heading * 10) / 10.0);
            point.setAccuracy(Math.round(3 + ThreadLocalRandom.current().nextDouble(0, 5) * 10) / 10.0);
            point.setTimestamp(timestamp);
            point.setCreatedAt(now);

            track.add(point);
        }

        gpsPointRepository.saveAll(track);
        System.out.println("Сохранено " + track.size() + " точек для машины " + carId);

        return track;
    }

    public List<GpsPoint> generateTrackInBoundingBox (UUID carId, double minLat, double maxLat, double minLon, double maxLon,
                                                      int pointsCount, int intervalSeconds, double speedMin, double speedMax) throws Exception {
        double startLat = minLat + random.nextDouble() * (maxLat - minLat);
        double startLon = minLon + random.nextDouble() * (maxLon - minLon);
        double endLat = minLat + random.nextDouble() * (maxLat - minLat);
        double endLon = minLon + random.nextDouble() * (maxLon - minLon);

        System.out.println("Трек между: (" + startLat + "," + startLon + ") и (" + endLat + "," + endLon + ")");

        return generateTrackByRoads(carId, startLat, startLon, endLat, endLon, pointsCount, intervalSeconds, speedMin, speedMax, 50.0, 300.0);
    }
}
