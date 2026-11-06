package com.taxiproject.mytaxi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MILEAGE")
public class MileageReport extends Report {
    @Column(name = "total_mileage")
    private Double totalMileage;

    public MileageReport() {

    }

    public MileageReport (String name, PeriodType periodType, java.time.LocalDate startDate, java.time.LocalDate endDate, Enterprise enterprise, Car car) {
        setName(name);
        setPeriodType(periodType);
        setStartDate(startDate);
        setEndDate(endDate);
        setEnterprise(enterprise);
        setCar(car);
    }

    public Double getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(Double totalMileage) {
        this.totalMileage = totalMileage;
    }
}
