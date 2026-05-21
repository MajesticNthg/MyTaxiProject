package com.taxiproject.mytaxi.entity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enterprises")
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private String city;

    private Integer employeeCount;

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Car> cars = new ArrayList<>();

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Driver> drivers = new ArrayList<>();

    public Enterprise() {}

    public Enterprise(String name, String city, Integer employeeCount) {
        this.name = name;
        this.city = city;
        this.employeeCount = employeeCount;
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

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }
}
