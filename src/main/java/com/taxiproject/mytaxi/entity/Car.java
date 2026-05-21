package com.taxiproject.mytaxi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (unique = true)
    private String licensePlate;

    private String yearOfRelease;

    private String carMileage;

    private Integer price;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @ManyToMany
    @JoinTable (
            name = "car_drivers",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "driver_id")
    )
    private List<Driver> drivers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "active_driver_id")
    private Driver activeDriver;

    public Car() {}

    public Car(String licensePlate, String yearOfRelease, String carMileage, Integer price, Brand brand, Enterprise enterprise) {
        this.licensePlate = licensePlate;
        this.yearOfRelease = yearOfRelease;
        this.carMileage = carMileage;
        this.price = price;
        this.brand = brand;
        this.enterprise = enterprise;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public void setPrice (Integer Price) {
        this.price = Price;
    }

    public Brand getBrand() { return brand; }

    public void setBrand(Brand brand) { this.brand = brand; }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Driver getActiveDriver() {
        return activeDriver;
    }

    public void setActiveDriver(Driver activeDriver) {
        this.activeDriver = activeDriver;
    }
}
