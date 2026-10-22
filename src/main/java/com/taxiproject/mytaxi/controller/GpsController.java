package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.GpsPointDto;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.service.EnterpriseService;
import com.taxiproject.mytaxi.service.CarService;
import com.taxiproject.mytaxi.service.GpsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gps")
public class GpsController {
    private final GpsService gpsService;
    private final EnterpriseRepository enterpriseRepository;
    private final CarService carService;

    public GpsController(GpsService gpsService, EnterpriseRepository enterpriseRepository, CarService carService) {
        this.gpsService = gpsService;
        this.enterpriseRepository = enterpriseRepository;
        this.carService = carService;
    }

    @GetMapping("/points")
    public ResponseEntity<?> getPoints (@RequestParam UUID carId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                        @RequestParam(defaultValue = "json") String format,
                                        @RequestParam(defaultValue = "false") boolean track) {
        Car car = carService.getCarById(carId);
        Enterprise enterprise = car.getEnterprise();
        String timezone = enterprise.getTimezone() != null ? enterprise.getTimezone() : "UTC";

        if ("geoJson".equalsIgnoreCase(format)) {
            Object geoJson = gpsService.getPointsAsGeoJson(carId, from, to, timezone, track);
            return ResponseEntity.ok(geoJson);
        }

        List<GpsPointDto> points = gpsService.getPointsAsJson(carId, from, to, timezone);
        return ResponseEntity.ok(points);
    }
}
