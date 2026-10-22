package com.taxiproject.mytaxi.Dto;

import java.util.UUID;

public class CarSimpleDto {
    private UUID id;
    private String licencePlate;

    public CarSimpleDto() {}

    public CarSimpleDto(UUID id, String licencePlate) {
        this.id = id;
        this.licencePlate = licencePlate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }
}
