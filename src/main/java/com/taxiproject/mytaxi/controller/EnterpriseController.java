package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.service.EnterpriseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/enterprises")
public class EnterpriseController {

    public final EnterpriseService enterpriseService;

    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @GetMapping
    public String listEnterprises(Model model) {
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "enterprises-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("enterprise", new Enterprise());
        return "create-enterprises-form";
    }

    @PostMapping("/save")
    public String saveEnterprise(@ModelAttribute Enterprise enterprise) {
        enterpriseService.saveEnterprise(enterprise);
        return "redirect:/enterprises";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm (@PathVariable Integer id, Model model) {
        Enterprise enterprise = enterpriseService.getEnterpriseById(id);
        model.addAttribute("enterprise", enterprise);
        return "enterprise-edit";
    }

    @PostMapping("/update/{id}")
    public String updateEnterprise(@PathVariable Integer id, @ModelAttribute Enterprise enterprise) {
        enterprise.setId(id);
        enterpriseService.updateEnterprise(enterprise);
        return "redirect:/enterprises";
    }

    @GetMapping("/delete/{id}")
    public String deleteEnterprise(@PathVariable Integer id) {
        enterpriseService.deleteEnterprise(id);
        return "redirect:/enterprises";
    }

}
