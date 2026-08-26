package com.taxiproject.mytaxi.Dto;

public class CarSimpleDto {
    private Integer id;
    private String licencePlate;

    public CarSimpleDto() {}

    public CarSimpleDto(Integer id, String licencePlate) {
        this.id = id;
        this.licencePlate = licencePlate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }
}
