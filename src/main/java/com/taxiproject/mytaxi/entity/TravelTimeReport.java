package com.taxiproject.mytaxi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("TRAVEL_TIME")
public class TravelTimeReport extends Report {
    @Column(name = "total_time_minutes")
    private Double totalTimeMinutes;

    public TravelTimeReport(String name, PeriodType periodType, LocalDate startDate,LocalDate endDate, Enterprise enterprise, Car car) {
        setName(name);
        setPeriodType(periodType);
        setStartDate(startDate);
        setEndDate(endDate);
        setEnterprise(enterprise);
        setCar(car);
    }

    public Double getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(Double totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }
}
