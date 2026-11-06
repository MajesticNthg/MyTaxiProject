package com.taxiproject.mytaxi.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public class ReportResponseDto {
    private UUID reportId;
    private String reportType;
    private String name;
    private UUID carId;
    private String carLicensePlate;
    private UUID enterpriseId;
    private String enterpriseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String periodType;
    private List<ReportItemDto> items;

    public ReportResponseDto() {

    }

    public ReportResponseDto (UUID reportId, String reportType, String name, UUID carId, String carLicensePlate, UUID enterpriseId, String enterpriseName,
                              LocalDate startDate, LocalDate endDate, String periodType, List<ReportItemDto> items) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.name = name;
        this.carId = carId;
        this.carLicensePlate = carLicensePlate;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodType = periodType;
        this.items = items;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public void setCarLicensePlate(String carLicensePlate) {
        this.carLicensePlate = carLicensePlate;
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public UUID getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(UUID enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public List<ReportItemDto> getItems() {
        return items;
    }

    public void setItems(List<ReportItemDto> items) {
        this.items = items;
    }

    public static class ReportItemDto {
        private String period;
        private Double value;
        private String unit;

        public ReportItemDto() {

        }

        public ReportItemDto(String period, Double value, String unit) {
            this.period = period;
            this.value = value;
            this.unit = unit;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
