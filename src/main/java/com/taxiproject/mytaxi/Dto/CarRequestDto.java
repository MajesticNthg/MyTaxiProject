package com.taxiproject.mytaxi.Dto;
import java.util.List;
import jakarta.validation.constraints.NotBlank;

public class CarRequestDto {
    @NotBlank
    private String licensePlate;
    private String yearOfRelease;
    private String carMileage;
    private Integer price;
    private Integer brandId;
    private Integer enterpriseId;
    private List<Integer> driverIds;
    private Integer activeDriverId;

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

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public List<Integer> getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(List<Integer> driverIds) {
        this.driverIds = driverIds;
    }

    public Integer getActiveDriverId() {
        return activeDriverId;
    }

    public void setActiveDriverId(Integer activeDriverId) {
        this.activeDriverId = activeDriverId;
    }
}
