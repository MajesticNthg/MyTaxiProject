package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.Dto.CarResponseDto;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.DriverRepository;
import com.taxiproject.mytaxi.repository.BrandRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.projection.CarWithBrandIdProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Page<Car> getCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return carRepository.findAll(pageable);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(UUID id) {
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            throw new RuntimeException("Машина с ID" + id + " не найдена");
        }
        return car;
    }

    public void saveCar (Car car, UUID brandId, UUID enterpriseId, List<UUID> driversIds) {
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

        carRepository.save(car);
    }

    public void updateCar(Car car, UUID brandId, UUID enterpriseId, List<UUID> driverIds, UUID activeDriverId) {
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

        if (activeDriverId != null) {
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

    @Transactional
    public void deleteCar(UUID Id) {
        Car car = carRepository.findById(Id).orElseThrow(() -> new RuntimeException("Машина с ID " + Id + " не найдена"));
        if (car.getActiveDriver() != null) {
            throw new RuntimeException("Нельзя удалить машину с активным водителем " + car.getActiveDriver().getId());
        }

        carRepository.deleteCarDriversByCarId(Id);
        carRepository.deleteCarById(Id);
    }

    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Бренд с ID " + id + " не найден"));
    }

    public Enterprise getEnterpriseById(UUID id) {
        return enterpriseRepository.findById(id).orElseThrow(() -> new RuntimeException("Предприятия с ID " + id + " не найдено"));
    }

    public Page<CarResponseDto> getCarsByEnterprisePaginated(UUID enterpriseId, int page, int size, String sortBy, String sortDirection) {
        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 20;
        }

        if (sortDirection == null || sortDirection.isEmpty()) {
            sortDirection = "asc";
        }
        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            sortDirection = "asc";
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page <Car> carPage = carRepository.findCarsByEnterpriseId(enterpriseId, pageable);
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new RuntimeException("Предприятие не найдено"));
        String timezone = enterprise.getTimezone() != null ? enterprise.getTimezone() : "UTC";

        return carPage.map(car -> new CarResponseDto(car, timezone));
    }

}
