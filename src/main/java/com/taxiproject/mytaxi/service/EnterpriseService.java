package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.entity.Manager;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.repository.ManagerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final ManagerRepository managerRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository, ManagerRepository managerRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.managerRepository = managerRepository;
    }

    public void checkAccessToEnterprise(Integer enterpriseId, String username) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предприятие не найдено"));

        boolean hasAccess = enterprise.getManagers().stream()
                .anyMatch(m -> m.getUsername().equals(username));

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "У вас нет доступа к этому предприятию");
        }
    }

    public List<Enterprise> getEnterprisesForManager(String username) {
        Manager manager = managerRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Менеджер не найден"));
        return manager.getEnterprises();
    }

    public List<Enterprise> getAllEnterprises() {
        return enterpriseRepository.findAll();
    }

    public Enterprise getEnterpriseById (Integer id) {
        Enterprise enterprise = enterpriseRepository.findById(id).orElse(null);
        if (enterprise == null) {
            throw new RuntimeException("ПРедприятие с ID" + id + " не найдено");
        }
        return enterprise;
    }

    public void saveEnterprise (Enterprise enterprise) {
        enterpriseRepository.save(enterprise);
    }

    public void updateEnterprise (Enterprise enterprise) {
        if (!enterpriseRepository.existsById(enterprise.getId())) {
            throw new RuntimeException("Предприятие с ID" + enterprise.getId() + " не найденО");
        }
        enterpriseRepository.save(enterprise);
    }

    public void deleteEnterprise(Integer id) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предприятие не найдено"));

        if (!enterprise.getCars().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Невозможно удалить предприятие, так как за ним закреплены автомобили");
        }
        if (!enterprise.getDrivers().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Невозможно удалить предприятие, так как за ним закреплены водители");
        }

        enterpriseRepository.deleteById(id);
    }
}
