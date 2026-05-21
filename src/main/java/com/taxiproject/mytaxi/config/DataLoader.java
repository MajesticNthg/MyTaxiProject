package com.taxiproject.mytaxi.config;

import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.repository.BrandRepository;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.service.EnterpriseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final CarRepository carRepository;

    private final BrandRepository brandRepository;

    private final EnterpriseRepository enterpriseRepository;

    public DataLoader(CarRepository carRepository, BrandRepository brandRepository, EnterpriseRepository enterpriseRepository) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    @Override
    public void run (String... args) throws Exception {
        if (brandRepository.count() == 0) {
            Brand toyota = new Brand(
                    "Toyota", "Седан", 4, 1.6, 2.5, 150
            );
            Brand nissan = new Brand(
                    "Nissan", "Универсал", 4, 1.8, 1.4, 140
            );
            Brand honda = new Brand(
                    "Honda", "Кроссовер", 4, 2.0, 2.0, 160
            );

            brandRepository.save(toyota);
            brandRepository.save(nissan);
            brandRepository.save(honda);
        }

        if (enterpriseRepository.count() == 0) {
            Enterprise TaxiMoscow = new Enterprise("Такси Москва", "Москва", 150);
            Enterprise TaxiSaintPetersburg = new Enterprise("Такси Санкт-Петербург", "Санкт-Петербург", 120);
            Enterprise TaxiYekaterinburg = new Enterprise("Такси Екатерингбург", "Екатеринбург", 90);

            enterpriseRepository.save(TaxiMoscow);
            enterpriseRepository.save(TaxiSaintPetersburg);
            enterpriseRepository.save(TaxiYekaterinburg);
        }

        if (carRepository.count() == 0) {

            Brand toyota = brandRepository.findByBrandName("Toyota").orElse(null);
            Brand nissan = brandRepository.findByBrandName("Nissan").orElse(null);
            Brand honda = brandRepository.findByBrandName("Honda").orElse(null);

            Enterprise taxiMoscow = enterpriseRepository.findById(1).orElse(null);
            Enterprise taxiSaintPetersburg = enterpriseRepository.findById(2).orElse(null);
            Enterprise taxiYekaterinburg = enterpriseRepository.findById(3).orElse(null);

            Car car1 = new Car(
                    "A123BC", "1994", "276322", 393000, toyota, taxiMoscow
            );

            Car car2 = new Car(
                    "A123TC", "2014", "200394", 955000, nissan, taxiSaintPetersburg
            );

            Car car3 = new Car(
                    "T404AB", "2005", "145672", 544000, honda, taxiYekaterinburg
            );

            carRepository.save(car1);
            carRepository.save(car2);
            carRepository.save(car3);
        }
    }
}
