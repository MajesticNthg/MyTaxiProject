package com.taxiproject.mytaxi.Dto;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public class TripResponseDto {
    private Long tripId;
    private Integer carId;
    private String startTime;
    private String endTime;
    private List<GpsPointDto> points;

    public TripResponseDto() {}

    public TripResponseDto(Long tripId, Integer carId, String startTime, String endTime, List<GpsPointDto> points) {
        this.tripId = tripId;
        this.carId = carId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.points = points;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
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


