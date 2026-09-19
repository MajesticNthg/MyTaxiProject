package com.taxiproject.mytaxi.util;

import com.taxiproject.mytaxi.entity.GpsPoint;
import com.taxiproject.mytaxi.service.GpsTrackGeneratorService;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GpsCommands {

    private final GpsTrackGeneratorService generatorService;

    public GpsCommands(GpsTrackGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @Command(name = "generate-track", description = "Генерирует трек для автомобиля по дорогам между двумя точками", group = "MyTaxi", help = """
           Генерирует трек по дорогам через GraphHopper, пример - generate-track -c 1 --start-lat 55.7558 --start-lon 37.6173 --end-lat 55.7565 --end.lon 37.6190 -n 50 -i 5
           """)
    public String generateTrack(@Option(shortName = 'c', longName = "carId", description = "ID автомобиля", required = true) Integer carId,
                                @Option(longName = "start-lat", description = "Начальная широта", required = true) double startLat,
                                @Option(longName = "start-lon", description = "Начальная долгота", required = true) double startLon,
                                @Option(longName = "end-lat", description = "Конечная широта", required = true) double endLat,
                                @Option(longName = "end-lon", description = "Конечная долгота", required = true) double endLon,
                                @Option(shortName = 'n', longName = "points", description = "Количество точек", defaultValue = "100") int pointsCount,
                                @Option(shortName = 'i', longName = "interval", description = "Интервал между точками (сек)", defaultValue = "10") int intervalSeconds,
                                @Option(longName = "speed-min", description = "Минимальная скорость (км/ч)", defaultValue = "20") double speedMin,
                                @Option(longName = "speed-max", description = "Максимальная скорость (км/ч)", defaultValue = "80") double speedMax) {
        try {
            List<GpsPoint> track = generatorService.generateTrackByRoads(carId, startLat, startLon, endLat, endLon, pointsCount, intervalSeconds, speedMin, speedMax, 50.0, 300.0);
            return "Сгенерировано и сохранено " + track.size() + " точек для машины с ID: " + carId;
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    @Command(name = "generate-track-in-city", description = "Генерирует случайный трек внутри указанного города", group = "MyTaxi", help = """
    Генерирует случайный трек внутри города. Пример: generate-track-in-city -c 1 --city Moscow -n 100 -i 10
""")
    public String generateTrackInCity(
            @Option(shortName = 'c', longName = "carId", description = "ID автомобиля", required = true) Integer carId,
            @Option(longName = "city", description = "Город", defaultValue = "Moscow") String city,
            @Option(shortName = 'n', longName = "points", description = "Количество точек", defaultValue = "100") int pointsCount,
            @Option(shortName = 'i', longName = "interval", description = "Интервал между точками (сек)", defaultValue = "10") int intervalSeconds) {
        try {
            double minLat, maxLat, minLon, maxLon;
            switch (city.toLowerCase()) {
                case "moscow" -> {
                    minLat = 55.55;
                    maxLat = 55.90;
                    minLon = 37.35;
                    maxLon = 37.80;
                }
                case "spb" -> {
                    minLat = 59.80;
                    maxLat = 60.10;
                    minLon = 30.10;
                    maxLon = 30.50;
                }
                case "ekaterinburg" -> {
                    minLat = 56.70;
                    maxLat = 56.95;
                    minLon = 60.50;
                    maxLon = 60.85;
                }
                default -> throw new IllegalArgumentException("Неизвестный город: " + city);
            }

            List<GpsPoint> track = generatorService.generateTrackInBoundingBox(carId, minLat, maxLat, minLon, maxLon, pointsCount, intervalSeconds, 20, 80);
            return "Сгенерировано " + track.size() + " точек для машины " + carId + " в городе " + city;
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

}
