package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.service.BrandService;
import com.taxiproject.mytaxi.entity.Brand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController (BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public String listBrands (Model model) {
        model.addAttribute("brands", brandService.getAllBrands());
        return "brands-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "create-brand-form";
    }

    @PostMapping("/save")
    public String saveBrand(@ModelAttribute Brand brand) {
        brandService.saveBrand(brand);
        return "redirect:/brands";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Brand brand = brandService.getBrandById(id);
        model.addAttribute("brand", brand);
        return "brand-edit";
    }

    @PostMapping("/update/{id}")
    public String updateBrand(@PathVariable Integer id, @ModelAttribute Brand brand) {
        brand.setId(id);
        brandService.updateBrand(brand);
        return "redirect:/brands";
    }

    @GetMapping("delete/{id}")
    public String deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return "redirect:/brands";
    }
}
