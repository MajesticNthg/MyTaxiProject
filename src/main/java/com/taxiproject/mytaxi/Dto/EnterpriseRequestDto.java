package com.taxiproject.mytaxi.Dto;

import java.util.List;
import java.util.UUID;

public class EnterpriseRequestDto {
    private String name;
    private String city;
    private Integer employeeCount;
    private List<UUID> managerIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public List<UUID> getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(List<UUID> managerIds) {
        this.managerIds = managerIds;
    }
}
