package com.taxiproject.mytaxi.Dto;

import java.util.UUID;

public class GeoJsonProperties {
    private UUID id;
    private Double speed;
    private Double altitude;
    private String timestamp;

    public GeoJsonProperties() {}

    public GeoJsonProperties(UUID id, Double speed, Double altitude, String timestamp) {
        this.id = id;
        this.speed = speed;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
