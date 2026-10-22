package com.taxiproject.mytaxi.Dto;

import com.opencsv.bean.CsvBindByName;

import java.util.UUID;

public class TripExportDto {
    @CsvBindByName(column = "id")
    private UUID id;

    @CsvBindByName(column = "car_id")
    private UUID carId;

    @CsvBindByName(column = "start_time")
    private String startTime;

    @CsvBindByName(column = "end_time")
    private String endTime;

    @CsvBindByName(column = "start_lat")
    private Double startLat;

    @CsvBindByName(column = "start_lon")
    private Double startLon;

    @CsvBindByName(column = "start_address")
    private String startAddress;

    @CsvBindByName(column = "end_lat")
    private Double endLat;

    @CsvBindByName(column = "end_lon")
    private Double endLon;

    @CsvBindByName(column = "end_address")
    private String endAddress;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
