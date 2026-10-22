package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.service.DriverService;
import com.taxiproject.mytaxi.service.EnterpriseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;
    private final EnterpriseService enterpriseService;

    public DriverController(DriverService driverService, EnterpriseService enterpriseService) {
        this.driverService = driverService;
        this.enterpriseService = enterpriseService;
    }

    @GetMapping
    public String listDrivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "driver-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("driver", new Driver());
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "create-driver-form";
    }

    @PostMapping("/save")
    public String saveDriver(@ModelAttribute Driver driver, @RequestParam UUID enterpriseId) {
        driverService.saveDriver(driver, enterpriseId);
        return "redirect:/drivers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Driver driver = driverService.getDriverById(id);
        model.addAttribute("driver", driver);
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "driver-edit";
    }

    @PostMapping("/update/{id}")
    public String updateDriver(@PathVariable UUID id, @ModelAttribute Driver driver, @RequestParam UUID enterpriseId) {
        driver.setId(id);
        driverService.updateDriver(driver, enterpriseId);
        return "redirect:/drivers";
    }

    @GetMapping("/delete/{id}")
    public String deleteDriver (@PathVariable UUID id) {
        driverService.deleteDriver(id);
        return "redirect:/drivers";
    }
}
