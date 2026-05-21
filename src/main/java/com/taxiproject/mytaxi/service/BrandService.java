package com.taxiproject.mytaxi.service;

import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.repository.BrandRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id).orElse(null);
        if (brand == null) {
            throw new RuntimeException("Бренд с ID" + id + " не найден");
        }
        return brand;
    }

    public void saveBrand(Brand brand) {
        brandRepository.save(brand);
    }

    public void updateBrand (Brand brand) {
        if (!brandRepository.existsById(brand.getId())) {
            throw new RuntimeException("Бренд с ID" + brand.getId() + " не найден");
        }
        brandRepository.save(brand);
    }

    public void deleteBrand(Integer id) {
        brandRepository.deleteById(id);
    }
}
