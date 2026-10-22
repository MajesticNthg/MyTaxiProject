package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.BrandRepository;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.util.RandomCarFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CarGeneratorService {

    private final CarRepository carRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final BrandRepository brandRepository;
    private final RandomCarFactory randomCarFactory;
    private final Random random = new Random();

    public CarGeneratorService (CarRepository carRepository, EnterpriseRepository enterpriseRepository, BrandRepository brandRepository, RandomCarFactory randomCarFactory) {
        this.carRepository = carRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.brandRepository = brandRepository;
        this.randomCarFactory = randomCarFactory;
    }

    @Transactional
    public void generateCars(UUID enterpriseId, int count) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new RuntimeException("Предприятие с ID " + enterpriseId + " не найдено"));

        List<Brand> brands = brandRepository.findAll();

        if (brands.isEmpty()) {
            throw new RuntimeException("Бренды не найдены");
        }

        List<Driver> drivers = enterprise.getDrivers();
        if (drivers.isEmpty()) {
            throw new RuntimeException("Водители у данного предприятия отсутствуют");
        }

        List<Car> createdCars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Brand randomBrand = brands.get(random.nextInt(brands.size()));
            Car car = randomCarFactory.createRandomCar(randomBrand, enterprise, new ArrayList<>(drivers));
            carRepository.save(car);
            createdCars.add(car);
        }

        enterprise.getCars().addAll(createdCars);
        enterpriseRepository.save(enterprise);
    }
}
