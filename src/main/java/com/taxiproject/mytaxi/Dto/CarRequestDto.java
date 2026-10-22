package com.taxiproject.mytaxi.Dto;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class CarRequestDto {
    @NotBlank
    private String licensePlate;
    private String yearOfRelease;
    private String carMileage;
    private Integer price;
    private UUID brandId;
    private UUID enterpriseId;
    private List<UUID> driverIds;
    private UUID activeDriverId;
    private String purchaseDateTime;

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

    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public UUID getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(UUID enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public List<UUID> getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(List<UUID> driverIds) {
        this.driverIds = driverIds;
    }

    public UUID getActiveDriverId() {
        return activeDriverId;
    }

    public void setActiveDriverId(UUID activeDriverId) {
        this.activeDriverId = activeDriverId;
    }

    public String getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(String purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }
}
