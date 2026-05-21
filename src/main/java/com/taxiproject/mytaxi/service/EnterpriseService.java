package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
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
        enterpriseRepository.deleteById(id);
    }
}
