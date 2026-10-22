package com.taxiproject.mytaxi.projection;

import java.util.UUID;

public interface DriversIdProjection {
    UUID getId();
    String getName();
    Integer getAge();
    Integer getSalary();
    UUID getEnterpriseId();
}
