package com.taxiproject.mytaxi.Dto;

import java.util.List;

public class GeoJsonGeometry {
    private String type = "Point";
    private List<Double> coordinates;

    public GeoJsonGeometry() {}

    public GeoJsonGeometry(String type, List<Double> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
