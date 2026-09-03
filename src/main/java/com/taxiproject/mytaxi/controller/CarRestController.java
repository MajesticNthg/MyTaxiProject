package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.CarResponseDto;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.Dto.CarRequestDto;
import com.taxiproject.mytaxi.projection.CarWithBrandIdProjection;
import com.taxiproject.mytaxi.service.CarService;
import com.taxiproject.mytaxi.repository.CarRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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

    @PostMapping
    public ResponseEntity<Car> createCar(@Valid @RequestBody CarRequestDto request) {
        Car car = new Car();
        car.setLicensePlate(request.getLicensePlate());
        car.setYearOfRelease(request.getYearOfRelease());
        car.setCarMileage(request.getCarMileage());
        car.setPrice(request.getPrice());

        if (request.getPurchaseDateTime() != null) {
            car.setPurchaseDateTime(LocalDateTime.parse(request.getPurchaseDateTime(), DateTimeFormatter.ISO_DATE_TIME));
        }

        if (request.getBrandId() != null) {
            car.setBrand(carService.getBrandById(request.getBrandId()));
        }

        if (request.getEnterpriseId() != null) {
            car.setEnterprise(carService.getEnterpriseById(request.getEnterpriseId()));
        }

        carService.saveCar(car, request.getBrandId(), request.getEnterpriseId(), request.getDriverIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCar(@PathVariable Integer id, @RequestBody CarRequestDto request) {
        Car existingCar = carService.getCarById(id);

        existingCar.setLicensePlate(request.getLicensePlate());
        existingCar.setYearOfRelease(request.getYearOfRelease());
        existingCar.setCarMileage(request.getCarMileage());
        existingCar.setPrice(request.getPrice());

        if (request.getPurchaseDateTime() != null) {
            existingCar.setPurchaseDateTime(LocalDateTime.parse(request.getPurchaseDateTime(), DateTimeFormatter.ISO_DATE_TIME));
        } else {
            existingCar.setPurchaseDateTime(null);
        }

        if (request.getBrandId() != null) {
            existingCar.setBrand(carService.getBrandById(request.getBrandId()));
        } else {
            existingCar.setBrand(null);
        }

        if (request.getEnterpriseId() != null) {
            existingCar.setEnterprise(carService.getEnterpriseById(request.getEnterpriseId()));
        } else {
            existingCar.setEnterprise(null);
        }

        carService.updateCar(existingCar, request.getBrandId(), request.getEnterpriseId(), request.getDriverIds(), request.getActiveDriverId());
        return ResponseEntity.ok(Map.of("id", id, "message", "Машина успешно обновлена"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Integer id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("активным водителем")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", message));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", message));
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CarResponseDto>> getCarPaginated (@RequestParam Integer enterpriseId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
                                                                 @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<CarResponseDto> cars = carService.getCarsByEnterprisePaginated(enterpriseId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(cars);
    }

}
