package com.taxiproject.mytaxi.config;

import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.entity.Manager;
import com.taxiproject.mytaxi.repository.BrandRepository;
import com.taxiproject.mytaxi.repository.CarRepository;
import com.taxiproject.mytaxi.repository.EnterpriseRepository;
import com.taxiproject.mytaxi.repository.ManagerRepository;
import com.taxiproject.mytaxi.repository.DriverRepository;
import com.taxiproject.mytaxi.service.EnterpriseService;
import com.taxiproject.mytaxi.service.ManagerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final CarRepository carRepository;

    private final BrandRepository brandRepository;

    private final EnterpriseRepository enterpriseRepository;

    private final ManagerRepository managerRepository;

    private final ManagerService managerService;

    private final DriverRepository driverRepository;

    public static Integer TEST_ENTERPRISE_ID;

    public DataLoader(CarRepository carRepository, BrandRepository brandRepository, EnterpriseRepository enterpriseRepository, ManagerRepository managerRepository, ManagerService managerService, DriverRepository driverRepository) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.managerRepository = managerRepository;
        this.managerService = managerService;
        this.driverRepository = driverRepository;
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


        Enterprise testEnterprise = enterpriseRepository.findByName("Тестовое предприятие")
                .orElseGet(() -> enterpriseRepository.save(
                        new Enterprise("Тестовое предприятие", "Тестовый город", 10)
                ));
//
//        if (enterpriseRepository.findByName("Тестовое предприятие").isEmpty()) {
//            System.out.println("создаем предприятие пустое");
//            Enterprise testEnterprise = new Enterprise("Тестовое предприятие", "Тестовый город", 10);
//            enterpriseRepository.save(testEnterprise);
//            System.out.println("предприятие создано");
//        } else {
//            System.out.println("предприятие не создано (пустое)");
//        }

        if (driverRepository.count() == 0) {
            Driver driver1 = new Driver("Иван Петров", 35, 80000);
            Driver driver2 = new Driver("Сергей Сидоров", 40, 90000);
            driverRepository.save(driver1);
            driverRepository.save(driver2);
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

        if (managerRepository.findByUsername("DanilKhomenko").isEmpty()) {
            Manager admin = new Manager ("DanilKhomenko", "", "Хоменко Данил Игоревич", "KhomenkoDI@mail.ru", "ROLE_ADMIN");
            managerService.saveManager(admin, "admin123");
        }

        if (managerRepository.findByUsername("SergeyIvanov").isEmpty()) {
            Manager user = new Manager ("SergeyIvanov", "", "Иванов Сергей Андреевич", "IvanovSI@mail.ru", "ROLE_USER");
            managerService.saveManager(user, "user123");
        }

        if (managerRepository.findByUsername("TestManager1").isEmpty()) {
            Manager manager1 = new Manager("TestManager1", "", "Тестовый Менеджер 1",
                    "manager1@taxi.ru", "ROLE_ADMIN");
            managerService.saveManager(manager1, "pass123");
            testEnterprise.getManagers().add(manager1);
        }

        if (managerRepository.findByUsername("TestManager2").isEmpty()) {
            Manager manager2 = new Manager("TestManager2", "", "Тестовый Менеджер 2",
                    "manager2@taxi.ru", "ROLE_ADMIN");
            managerService.saveManager(manager2, "pass123");
        }


        Manager manager1 = managerRepository.findByUsername("manager1").orElse(null);
        Manager manager2 = managerRepository.findByUsername("manager2").orElse(null);

    }
}
