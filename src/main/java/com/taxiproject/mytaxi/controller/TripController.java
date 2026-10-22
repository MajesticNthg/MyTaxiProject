package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.GpsPointDto;
import com.taxiproject.mytaxi.Dto.TripListDto;
import com.taxiproject.mytaxi.Dto.TripResponseDto;
import com.taxiproject.mytaxi.service.TripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<List<TripResponseDto>> getTrips (
            @RequestParam UUID carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to)
    {
        List<TripResponseDto> trips = tripService.getTripWithPoints(carId, from, to);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/track")
    public ResponseEntity<List<GpsPointDto>> getTripsAsTrack (
            @RequestParam UUID carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<GpsPointDto> points = tripService.getTripAsSingleTrack(carId, from, to);
        return ResponseEntity.ok(points);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TripListDto>> getTripList (@RequestParam UUID carId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<TripListDto> trips = tripService.getTripList(carId, from, to);
        return ResponseEntity.ok(trips);
    }
}
