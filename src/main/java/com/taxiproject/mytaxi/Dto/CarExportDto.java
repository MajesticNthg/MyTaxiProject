package com.taxiproject.mytaxi.Dto;

import com.opencsv.bean.CsvBindByName;

import java.util.UUID;

public class CarExportDto {
    @CsvBindByName(column = "id")
    private UUID id;

    @CsvBindByName(column = "license_plate")
    private String licensePlate;

    @CsvBindByName(column = "year_of_release")
    private String yearOfRelease;

    @CsvBindByName(column = "car_mileage")
    private String carMileage;

    @CsvBindByName(column = "price")
    private Integer price;

    @CsvBindByName(column = "brand_name")
    private String brandName;

    @CsvBindByName(column = "enterprise_id")
    private UUID enterpriseId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(String yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public String getCarMileage() {
        return carMileage;
    }

    public void setCarMileage(String carMileage) {
        this.carMileage = carMileage;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public UUID getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(UUID enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
