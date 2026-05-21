package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.repository.BrandRepository;
import com.taxiproject.mytaxi.service.BrandService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandRestController {
    public final BrandService brandService;
    private final BrandRepository brandRepository;

    public BrandRestController (BrandService brandService, BrandRepository brandRepository) {
        this.brandService = brandService;
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/{id}")
    public Brand getBrandById(@PathVariable Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бренд с ID " + id + " не найден"));
    }
}
