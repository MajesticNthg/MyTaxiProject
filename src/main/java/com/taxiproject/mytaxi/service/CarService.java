package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.DriverRepository;
import com.taxiproject.mytaxi.repository.BrandRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final BrandRepository brandRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final DriverRepository driverRepository;

    public CarService(CarRepository carRepository, BrandRepository brandRepository, EnterpriseRepository enterpriseRepository, DriverRepository driverRepository) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.driverRepository = driverRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Integer id) {
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            throw new RuntimeException("Машина с ID" + id + " не найдена");
        }
        return car;
    }

    public void saveCar (Car car, Integer brandId, Integer enterpriseId, List<Integer> driversIds) {
        Brand brand = brandRepository.findById(brandId).orElse(null);
        if (brand == null) {
            throw new RuntimeException("Бренд не найден");
        }
        car.setBrand(brand);

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElse(null);
        if (enterprise == null) {
            throw new RuntimeException("Предприятие не найдено");
        }
        car.setEnterprise(enterprise);

//        if (driversIds != null && !driversIds.isEmpty()) {
//            List<Driver> drivers = driverRepository.findAllByIdIn(driversIds);
//            car.setDrivers(drivers);
//        } else {
//            car.setDrivers(new ArrayList<>());
//        }

        carRepository.save(car);
    }

    public void updateCar(Car car, Integer brandId, Integer enterpriseId, List<Integer> driverIds, Integer activeDriverId) {
        Brand brand = brandRepository.findById(brandId).orElse(null);
        if (brand == null) {
            throw new RuntimeException("Бренд не найден");
        }
        car.setBrand(brand);

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElse(null);
        if (enterprise == null) {
            throw new RuntimeException("Предприятие не найдено");
        }
        car.setEnterprise(enterprise);

        if (driverIds != null && !driverIds.isEmpty()) {
            List<Driver> drivers = driverRepository.findAllById(driverIds);
            car.setDrivers(drivers);
        } else {
            car.setDrivers(new ArrayList<>());
        }

        if (activeDriverId != null && activeDriverId > 0) {
            if (car.getDrivers().stream().noneMatch(d -> d.getId().equals(activeDriverId))) {
                throw new RuntimeException("Активный водитель должен быть среди привязанных к машине");
            }
            boolean isActiveElsewhere = carRepository.existsActiveDriverIdAndIdNot(activeDriverId, car.getId());
            if (isActiveElsewhere) {
                throw new RuntimeException("Этот водитель уже является активным на другой машине");
            }
            Driver activeDriver = driverRepository.findById(activeDriverId).orElse(null);
            if (activeDriver == null) {
                throw new RuntimeException("Водитель не найден");
            }
            car.setActiveDriver(activeDriver);
        }

        carRepository.save(car);
    }

    public void deleteCar(Integer Id) {
        carRepository.deleteById(Id);
    }
}
