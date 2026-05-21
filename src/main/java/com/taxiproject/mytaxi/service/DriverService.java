package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.DriverRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverService {

    public final DriverRepository driverRepository;
    private final EnterpriseRepository enterpriseRepository;

    public DriverService(DriverRepository driverRepository, EnterpriseRepository enterpriseRepository) {
        this.driverRepository = driverRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Integer id) {
        Driver driver = driverRepository.findById(id).orElse(null);
        if (driver == null) {
            throw new RuntimeException("Водитель с ID" + id + " не найден");
        }
        return driver;
    }

    public void saveDriver(Driver driver, Integer enterpriseId) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElse(null);
        if (enterprise == null) {
            throw new RuntimeException("Предприятие не найдено");
        }
        driver.setEnterprise(enterprise);

        driverRepository.save(driver);
    }

    public void updateDriver(Driver driver, Integer enterpriseId) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElse(null);
        if (enterprise == null) {
            throw new RuntimeException("Предприятие не найдено");
        }
        driver.setEnterprise(enterprise);
//        if (!driverRepository.existsById(driver.getId())) {
//            throw new RuntimeException("Водитель с ID" + driver.getId() + " не найден");
//        }
        driverRepository.save(driver);
    }

    public void deleteDriver(Integer id) {
        driverRepository.deleteById(id);
    }
}
