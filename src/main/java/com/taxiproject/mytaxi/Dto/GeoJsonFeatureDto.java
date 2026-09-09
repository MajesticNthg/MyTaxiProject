package com.taxiproject.mytaxi.Dto;

import java.util.List;

public class GeoJsonFeatureDto {
    private String type = "Feature";
    private GeoJsonGeometry geometry;
    private GeoJsonProperties properties;

    public GeoJsonFeatureDto() {}

    public GeoJsonFeatureDto(GeoJsonGeometry geometry,GeoJsonProperties properties) {
        this.type = "Feature";
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonGeometry geometry) {
        this.geometry = geometry;
    }

    public GeoJsonProperties getProperties() {
        return properties;
    }

    public void setProperties(GeoJsonProperties properties) {
        this.properties = properties;
    }
}
