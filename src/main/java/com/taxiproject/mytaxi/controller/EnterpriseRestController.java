package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.Dto.EnterpriseWithCarsAndDriversDto;
import com.taxiproject.mytaxi.Dto.DriverSimpleDto;
import com.taxiproject.mytaxi.Dto.CarSimpleDto;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.projection.EnterpriseProjection;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.service.EnterpriseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enterprises")
public class EnterpriseRestController {
    private final EnterpriseService enterpriseService;
    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseRestController(EnterpriseService enterpriseService, EnterpriseRepository enterpriseRepository) {
        this.enterpriseService = enterpriseService;
        this.enterpriseRepository = enterpriseRepository;
    }

    @GetMapping
    public List<EnterpriseProjection> getAllEnterprises() {
        return enterpriseRepository.findAllEnterprisesProjection();
    }

    @GetMapping("/{id}")
    public EnterpriseProjection getEnterpriseById(@PathVariable Integer id) {
        return enterpriseRepository.findEnterpriseByIdProjection(id);
    }

    @GetMapping("/withCarsAndDrivers/{id}")
    public EnterpriseWithCarsAndDriversDto getEnterpriseWithCarsAndDrivers(@PathVariable Integer id) {
        Enterprise enterprise = enterpriseService.getEnterpriseById(id);
        return convertToFullDto(enterprise);
    }

    private EnterpriseWithCarsAndDriversDto convertToFullDto(Enterprise enterprise) {
        List<CarSimpleDto> carDtos = enterprise.getCars().stream().map(car -> new CarSimpleDto(
                car.getId(),
                car.getLicensePlate()
        )).collect(Collectors.toList());

        List<DriverSimpleDto> driverDtos = enterprise.getDrivers().stream().map(driver -> new DriverSimpleDto(
                driver.getId(),
                driver.getName()
        )).collect(Collectors.toList());

        return new EnterpriseWithCarsAndDriversDto (
                enterprise.getId(),
                enterprise.getName(),
                enterprise.getCity(),
                enterprise.getEmployeeCount(),
                carDtos,
                driverDtos
        );
    }

//    @GetMapping("/full-json/{id}")
//    public String getEnterpriseFullJson(@PathVariable Integer id) {
//        return enterpriseRepository.findEnterpriseFullJson(id);
//    }

    @GetMapping(value = "/full-json/{id}", produces = "application/json")
    public ResponseEntity<String> getEnterpriseFullJson(@PathVariable Integer id) {

        String json = enterpriseRepository.findEnterpriseFullJson(id);

        if (json == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(json);
    }
}
