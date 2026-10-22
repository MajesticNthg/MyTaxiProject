package com.taxiproject.mytaxi.Dto;

import com.opencsv.bean.CsvBindByName;

import java.util.UUID;

public class EnterpriseExportDto {
    @CsvBindByName(column = "id")
    private UUID id;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByName(column = "employee_count")
    private Integer employeeCount;

    @CsvBindByName(column = "timezone")
    private String timezone;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
