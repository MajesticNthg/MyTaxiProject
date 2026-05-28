package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.service.CarService;
import com.taxiproject.mytaxi.service.BrandService;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.service.DriverService;
import com.taxiproject.mytaxi.service.EnterpriseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;
    private final BrandService brandService;
    private final EnterpriseService enterpriseService;
    private final DriverService driverService;

    public CarController(CarService carService, BrandService brandService, EnterpriseService enterpriseService, DriverService driverService) {
        this.carService = carService;
        this.brandService = brandService;
        this.enterpriseService = enterpriseService;
        this.driverService = driverService;
    }

    @GetMapping
    public String listCars(Model model) {
        model.addAttribute("cars", carService.getAllCars());
        model.addAttribute("brands", brandService.getAllBrands());
        return "car-list";
    }

    @GetMapping ("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
//        model.addAttribute("allDrivers", driverService.getAllDrivers());
        return "create-car-form";

    }

    @PostMapping("save")
    public String saveCar (@ModelAttribute Car car, @RequestParam Integer brandId, @RequestParam Integer enterpriseId, @RequestParam(required = false) List<Integer> driverIds) {
        carService.saveCar(car, brandId, enterpriseId, driverIds);
        return "redirect:/cars";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        model.addAttribute("car", car);
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        model.addAttribute("allDrivers", driverService.getAllDrivers());
        return "car-edit";
    }

    @PostMapping("/update/{id}")
    public String updateCar(@PathVariable Integer id, @ModelAttribute Car car, @RequestParam Integer brandId, @RequestParam Integer enterpriseId, @RequestParam(required = false) List<Integer> driverIds, @RequestParam(required = false) Integer activeDriverId) {
        car.setId(id);
        carService.updateCar(car, brandId, enterpriseId, driverIds, activeDriverId);
        return "redirect:/cars";
    }

    @GetMapping("delete/{id}")
    public String deleteCar(@PathVariable Integer id){
        carService.deleteCar(id);
        return "redirect:/cars";
    }
}
