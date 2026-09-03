package com.taxiproject.mytaxi.Dto;

import com.taxiproject.mytaxi.entity.Car;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class CarResponseDto {
    private Integer id;
    private String licensePlate;
    private String yearOfRelease;
    private String carMileage;
    private Integer price;
    private Integer brandId;
    private String brandName;
    private Integer enterpriseId;
    private String enterpriseName;
    private Integer activeDriverId;
    private String purchaseDateTime;

    public CarResponseDto(Car car, String enterpriseTimezone) {
        this.id = car.getId();
        this.licensePlate = car.getLicensePlate();
        this.yearOfRelease = car.getYearOfRelease();
        this.carMileage = car.getCarMileage();
        this.price = car.getPrice();

        if (car.getBrand() != null) {
            this.brandId = car.getBrand().getId();
            this.brandName = car.getBrand().getBrandName();
        }

        if (car.getEnterprise() != null) {
            this.enterpriseId = car.getEnterprise().getId();
            this.enterpriseName = car.getEnterprise().getName();
        }

        if (car.getActiveDriver() != null) {
            this.activeDriverId = car.getActiveDriver().getId();
        }

        if (car.getPurchaseDateTime() != null) {
            ZoneId enterpriseZone = ZoneId.of(enterpriseTimezone != null ? enterpriseTimezone : "UTC");
            ZonedDateTime utcTime = car.getPurchaseDateTime().atZone(ZoneId.of("UTC"));
            ZonedDateTime enterpriseTime = utcTime.withZoneSameInstant(enterpriseZone);
            this.purchaseDateTime = enterpriseTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public Integer getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getYearOfRelease() { return yearOfRelease; }
    public String getCarMileage() { return carMileage; }
    public Integer getPrice() { return price; }
    public Integer getBrandId() { return brandId; }
    public String getBrandName() { return brandName; }
    public Integer getEnterpriseId() { return enterpriseId; }
    public String getEnterpriseName() { return enterpriseName; }
    public Integer getActiveDriverId() { return activeDriverId; }
    public String getPurchaseDateTime() { return purchaseDateTime; }
}
