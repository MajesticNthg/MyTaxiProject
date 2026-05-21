package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.projection.CarWithBrandIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.query.JpqlQueryBuilder;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Car c WHERE c.activeDriver.id = :driverId AND c.id <> :carId")
    boolean existsActiveDriverIdAndIdNot(Integer driverId, Integer carId);

    @Query(value = """
        SELECT 
            c.id,
            c.license_plate as licensePlate,
            c.year_of_release as yearOfRelease,
            c.car_mileage as carMileage,
            c.price,
            COALESCE(c.active_driver_id, -1) as activeDriverId,
            c.brand_id as brandId
        FROM cars c
        LEFT JOIN drivers d ON c.active_driver_id = d.id;
        """, nativeQuery = true)
    List<CarWithBrandIdProjection> findAllCarsWithBrandId();

    @Query(value = """
        SELECT 
            c.id,
            c.license_plate as licensePlate,
            c.year_of_release as yearOfRelease,
            c.car_mileage as carMileage,
            c.price,
            COALESCE(c.active_driver_id, -1) as activeDriverId,
            c.brand_id as brandId
        FROM cars c
        LEFT JOIN drivers d ON c.active_driver_id = d.id
        WHERE c.id = :id
        """, nativeQuery = true)
    CarWithBrandIdProjection findCarByIdWithBrandId(@Param("id") Integer id);
    }
