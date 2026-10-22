package com.taxiproject.mytaxi.Dto;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.UUID;

public class TripResponseDto {
    private UUID tripId;
    private UUID carId;
    private String startTime;
    private String endTime;
    private List<GpsPointDto> points;

    public TripResponseDto() {}

    public TripResponseDto(UUID tripId, UUID carId, String startTime, String endTime, List<GpsPointDto> points) {
        this.tripId = tripId;
        this.carId = carId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.points = points;
    }

    public UUID getTripId() {
        return tripId;
    }

    public void setTripId(UUID tripId) {
        this.tripId = tripId;
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<GpsPointDto> getPoints() {
        return points;
    }

    public void setPoints(List<GpsPointDto> points) {
        this.points = points;
    }
}


