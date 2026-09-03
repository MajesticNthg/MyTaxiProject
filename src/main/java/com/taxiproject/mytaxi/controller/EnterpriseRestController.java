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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return enterpriseRepository.findAllEnterprisesProjection();
        } else {
            // Для USER — нужно отфильтровать предприятия
            List<Enterprise> enterprises = enterpriseService.getEnterprisesForManager(username);
            // Преобразуем в проекцию (придётся сделать вручную, так как проекция из репозитория)
            return enterprises.stream()
                    .map(this::convertToProjection)
                    .collect(Collectors.toList());
        }
    }

    @GetMapping("/{id}")
    public EnterpriseProjection getEnterpriseById(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        enterpriseService.checkAccessToEnterprise(id, username);
        return enterpriseRepository.findEnterpriseByIdProjection(id);
    }

    @GetMapping("/withCarsAndDrivers/{id}")
    public EnterpriseWithCarsAndDriversDto getEnterpriseWithCarsAndDrivers(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        enterpriseService.checkAccessToEnterprise(id, username);

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

        return new EnterpriseWithCarsAndDriversDto(
                enterprise.getId(),
                enterprise.getName(),
                enterprise.getCity(),
                enterprise.getEmployeeCount(),
                carDtos,
                driverDtos
        );
    }

    @GetMapping(value = "/full-json/{id}", produces = "application/json")
    public ResponseEntity<String> getEnterpriseFullJson(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        enterpriseService.checkAccessToEnterprise(id, username);

        String json = enterpriseRepository.findEnterpriseFullJson(id);

        if (json == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(json);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnterprise(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        enterpriseService.checkAccessToEnterprise(id, username);
        enterpriseService.deleteEnterprise(id);
        return ResponseEntity.noContent().build();
    }

    private EnterpriseProjection convertToProjection(Enterprise enterprise) {
        return new EnterpriseProjection() {
            @Override
            public Integer getId() {
                return enterprise.getId();
            }

            @Override
            public String getName() {
                return enterprise.getName();
            }

            @Override
            public String getCity() {
                return enterprise.getCity();
            }

            @Override
            public Integer getEmployeeCount() {
                return enterprise.getEmployeeCount();
            }

            @Override
            public String getTimezone() {
                return enterprise.getTimezone();
            }
        };

    }

    @PutMapping("/{id}/timezone")
    public ResponseEntity<?> updateTimezone(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request
    ) {
        String timezone = request.get("timezone");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        enterpriseService.checkAccessToEnterprise(id, username);

        Enterprise enterprise = enterpriseService.getEnterpriseById(id);
        enterprise.setTimezone(timezone);
        enterpriseRepository.save(enterprise);

        return ResponseEntity.ok(Map.of("message", "Таймзона обновлена"));
    }
}
