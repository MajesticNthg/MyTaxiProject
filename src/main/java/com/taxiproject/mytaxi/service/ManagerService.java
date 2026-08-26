package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.entity.Manager;
import com.taxiproject.mytaxi.repository.ManagerRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class ManagerService implements UserDetailsService {
    private final ManagerRepository managerRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerService (ManagerRepository managerRepository, EnterpriseRepository enterpriseRepository) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.enterpriseRepository = enterpriseRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return managerRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Менеджер не найден: " + username));
    }

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    public Manager getManagerById(Integer id) {
        return managerRepository.findById(id).orElseThrow(() -> new RuntimeException("Менеджер с ID " + id + " не найден"));
    }

    public void saveManager(Manager manager, String rawPassword) {
        manager.setPassword(passwordEncoder.encode(rawPassword));
        manager.setEnterprises(new ArrayList<>());
        managerRepository.save(manager);
    }

    public void updateManager(Manager manager, String rawPassword, List<Integer> enterpriseIds) {
        Manager existing = managerRepository.findById(manager.getId()).orElseThrow();
        existing.setUsername(manager.getUsername());
        existing.setFullName(manager.getFullName());
        existing.setEmail(manager.getEmail());
        if (rawPassword != null && !rawPassword.isEmpty()) {
            manager.setPassword(passwordEncoder.encode(rawPassword));
        }

        if (enterpriseIds != null && !enterpriseIds.isEmpty()) {
            List<Enterprise> enterprises = enterpriseRepository.findAllById(enterpriseIds);
            existing.setEnterprises(enterprises);
        } else {
            existing.setEnterprises(new ArrayList<>());
        }
        managerRepository.save(existing);
    }

    public void deleteManager(Integer id) {
        managerRepository.deleteById(id);
    }
}
