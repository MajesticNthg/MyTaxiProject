package com.taxiproject.mytaxi.Dto;

import java.util.List;

public class TripListDto {
    private Long tripId;
    private Integer carId;
    private String startTime;
    private String endTime;
    private Double startLat;
    private Double startLon;
    private String startAddress;
    private Double endLat;
    private Double endLon;
    private String endAddress;

    public TripListDto() {}

    public TripListDto (Long tripId, Integer carId, String startTime, String endTime, Double startLat, Double startLon, String startAddress, Double endLat, Double endLon, String endAddress) {
        this.tripId = tripId;
        this.carId = carId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLat = startLat;
        this.startLon = startLon;
        this.startAddress = startAddress;
        this.endLat = endLat;
        this.endLon = endLon;
        this.endAddress = endAddress;
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

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLon() {
        return startLon;
    }

    public void setStartLon(Double startLon) {
        this.startLon = startLon;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLon() {
        return endLon;
    }

    public void setEndLon(Double endLon) {
        this.endLon = endLon;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }
}
