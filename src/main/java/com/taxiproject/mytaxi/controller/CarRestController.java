package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.projection.CarWithBrandIdProjection;
import com.taxiproject.mytaxi.service.CarService;
import com.taxiproject.mytaxi.repository.CarRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/cars")
public class CarRestController {
    private final CarService carService;
    private final CarRepository carRepository;

    public CarRestController (CarService carService, CarRepository carRepository) {
        this.carService = carService;
        this.carRepository = carRepository;
    }

    @GetMapping
    public List<CarWithBrandIdProjection> getAllCars () {
        return carRepository.findAllCarsWithBrandId();
    }

    @GetMapping("/{id}")
    public CarWithBrandIdProjection getCarById (@PathVariable Integer id) {
        CarWithBrandIdProjection car = carRepository.findCarByIdWithBrandId(id);
        if (car == null) {
            throw new RuntimeException("Машина с ID" + id + " не найдена");
        }
        return car;
    }
}
