package com.taxiproject.mytaxi.Dto;

import java.util.List;

public class EnterpriseWithCarsAndDriversDto {
    private Integer id;
    private String name;
    private String city;
    private Integer employeeCount;
    private List<CarSimpleDto> cars;
    private List<DriverSimpleDto> drivers;

    public EnterpriseWithCarsAndDriversDto() {}

    public EnterpriseWithCarsAndDriversDto(Integer id, String name, String city, Integer employeeCount, List<CarSimpleDto> cars, List<DriverSimpleDto> drivers) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.employeeCount = employeeCount;
        this.cars = cars;
        this.drivers = drivers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public List<CarSimpleDto> getCars() {
        return cars;
    }

    public void setCars(List<CarSimpleDto> cars) {
        this.cars = cars;
    }

    public List<DriverSimpleDto> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<DriverSimpleDto> drivers) {
        this.drivers = drivers;
    }
}
