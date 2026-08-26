package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.entity.Manager;
import com.taxiproject.mytaxi.service.EnterpriseService;
import com.taxiproject.mytaxi.service.ManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public String showEditForm(@PathVariable Integer id, Model model) {
        Manager manager = managerService.getManagerById(id);
        model.addAttribute("manager", manager);
        model.addAttribute("enterprises", enterpriseService.getAllEnterprises());
        return "manager-edit";
    }

    @PostMapping("/update/{id}")
    public String updateManager(@PathVariable Integer id, @ModelAttribute Manager manager, @RequestParam(required = false) String password, @RequestParam(required = false) List<Integer> enterpriseIds) {
        manager.setId(id);
        managerService.updateManager(manager, password, enterpriseIds);
        return "redirect:/managers";
    }

    @GetMapping("/delete/{id}")
    public String deleteManager(@PathVariable Integer id) {
        managerService.deleteManager(id);
        return "redirect:/managers";
    }

}
