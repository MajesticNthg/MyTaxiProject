package com.taxiproject.mytaxi.util;

import com.taxiproject.mytaxi.service.CarGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.shell.standard.ShellComponent;
//import org.springframework.shell.standard.ShellMethod;
//import org.springframework.shell.standard.ShellOption;

import com.taxiproject.mytaxi.entity.Brand;
import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.entity.Enterprise;
import com.taxiproject.mytaxi.entity.Driver;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RandomCarFactory {

    private final Random random = new Random();

    public Car createRandomCar(Brand brand, Enterprise enterprise, List<Driver> availableDrivers) {
        Car car = new Car();

        car.setBrand(brand);
        car.setEnterprise(enterprise);

        car.setLicensePlate(generatePlate());

        car.setYearOfRelease(String.valueOf(2010 + random.nextInt(16)));

        car.setCarMileage(String.valueOf(random.nextInt(350000)));

        car.setPrice(500000 + random.nextInt(2500000));

        if (!availableDrivers.isEmpty()) {
            Collections.shuffle(availableDrivers);

            int driverCount = 1 + random.nextInt(Math.min(3, availableDrivers.size()));

            List<Driver> assignedDrivers = new ArrayList<>(availableDrivers.subList(0, driverCount));

            car.setDrivers(assignedDrivers);

            if (random.nextInt(10) == 0) {
                car.setActiveDriver(assignedDrivers.get(random.nextInt(assignedDrivers.size())));
            }
        }

        return car;
    }

    private String generatePlate() {
        String letters = "ABEKMHOPCTYX";

        return "" + letters.charAt(random.nextInt(letters.length())) + (100 + random.nextInt(900)) + letters.charAt(random.nextInt(letters.length())) + letters.charAt(random.nextInt(letters.length()));
    }

}
