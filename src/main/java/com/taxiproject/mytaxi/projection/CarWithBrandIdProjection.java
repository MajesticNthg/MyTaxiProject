package com.taxiproject.mytaxi.projection;

import java.util.UUID;

public interface CarWithBrandIdProjection {
    UUID getId();
    String getLicensePlate();
    Integer getYearOfRelease();
    Integer getCarMileage();
    Integer getPrice();
    UUID getBrandId();
    UUID getActiveDriverId();
}
