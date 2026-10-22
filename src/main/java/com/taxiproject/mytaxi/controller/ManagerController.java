package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Manager;
import com.taxiproject.mytaxi.service.EnterpriseService;
import com.taxiproject.mytaxi.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/managers")
public class ManagerController {

    private final ManagerService managerService;

    private final EnterpriseService enterpriseService;

    public ManagerController(ManagerService managerService, EnterpriseService enterpriseService) {
        this.managerService = managerService;
        this.enterpriseService = enterpriseService;
    }

    @GetMapping
    public String listManagers(Model model) {
        model.addAttribute("managers", managerService.getAllManagers());
        return "managers-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("manager", new Manager());
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "create-manager-form";
    }

    @PostMapping("/save")
    public String saveManager(@ModelAttribute Manager manager, @RequestParam String password) {
        managerService.saveManager(manager, password);
        return "redirect:/managers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Manager manager = managerService.getManagerById(id);
        model.addAttribute("manager", manager);
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "manager-edit";
    }

    @PostMapping("/update/{id}")
    public String updateManager(@PathVariable UUID id, @ModelAttribute Manager manager, @RequestParam(required = false) String password, @RequestParam(required = false) List<UUID> enterpriseIds) {
        manager.setId(id);
        managerService.updateManager(manager, password, enterpriseIds);
        return "redirect:/managers";
    }

    @GetMapping("/delete/{id}")
    public String deleteManager(@PathVariable UUID id) {
        managerService.deleteManager(id);
        return "redirect:/managers";
    }

}
