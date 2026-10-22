package com.taxiproject.mytaxi.projection;

import java.util.UUID;

public interface EnterpriseProjection {
    UUID getId();
    String getName();
    String getCity();
    Integer getEmployeeCount();
    String getTimezone();

}
