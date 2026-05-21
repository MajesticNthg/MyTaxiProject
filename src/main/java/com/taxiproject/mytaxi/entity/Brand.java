package com.taxiproject.mytaxi.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;


@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String brandName;

    private String vehicleType;

    private Integer numberOfSeats;

    private Double weight;

    private Double fuelTankCapacity;

    private Integer enginePower;

    public Brand() {

    }

    public Brand (String brandName, String vehicleType, Integer numberOfSeats, Double weight, Double fuelTankCapacity, Integer enginePower) {
        this.brandName = brandName;
        this.vehicleType = vehicleType;
        this.numberOfSeats = numberOfSeats;
        this.weight = weight;
        this.fuelTankCapacity = fuelTankCapacity;
        this.enginePower = enginePower;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getFuelTankCapacity() {
        return fuelTankCapacity;
    }

    public void setFuelTankCapacity(Double fuelTankCapacity) {
        this.fuelTankCapacity = fuelTankCapacity;
    }

    public Integer getEnginePower() {
        return enginePower;
    }

    public void setEnginePower(Integer enginePower) {
        this.enginePower = enginePower;
    }
}
