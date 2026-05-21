package com.taxiproject.mytaxi.projection;

public interface CarWithBrandIdProjection {
    Integer getId();
    String getLicensePlate();
    Integer getYearOfRelease();
    Integer getCarMileage();
    Integer getPrice();
    Long getBrandId();
    Integer getActiveDriverId();
}
