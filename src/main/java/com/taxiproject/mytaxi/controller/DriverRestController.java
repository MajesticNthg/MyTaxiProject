package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.projection.DriversIdProjection;
import com.taxiproject.mytaxi.repository.DriverRepository;
import com.taxiproject.mytaxi.service.DriverService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverRestController {
    public final DriverService driverService;
    public final DriverRepository driverRepository;

    public DriverRestController(DriverService driverService, DriverRepository driverRepository) {
        this.driverService = driverService;
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<DriversIdProjection> getAllDrivers() {
        return driverRepository.findAllDriversWithEnterpriseId();
    }

    @GetMapping("/{id}")
    public DriversIdProjection getDriverById(@PathVariable Integer id) {
        return driverRepository.findDriverByIdWithEnterpriseId(id);
    }
}
