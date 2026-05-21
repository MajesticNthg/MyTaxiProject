package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByBrandName (String name);
}
