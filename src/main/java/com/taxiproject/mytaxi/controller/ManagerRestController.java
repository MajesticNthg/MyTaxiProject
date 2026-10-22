package com.taxiproject.mytaxi.controller;

import com.taxiproject.mytaxi.repository.ManagerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/managers")
public class ManagerRestController {

    private final ManagerRepository managerRepository;

    public ManagerRestController(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<String> getManagerById(@PathVariable UUID id) {
        String json = managerRepository.findManagerWithEnterprisesById(id);
        if (json == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(json);
    }
}
