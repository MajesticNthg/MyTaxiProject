package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Car;
import com.taxiproject.mytaxi.projection.CarWithBrandIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.query.JpqlQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Car c WHERE c.activeDriver.id = :driverId AND c.id <> :carId")
    boolean existsActiveDriverIdAndIdNot(UUID driverId, UUID carId);
    boolean existsByLicensePlate(String licensePlate);

    @Query(value = """
        SELECT 
            c.id,
            c.license_plate as licensePlate,
            c.year_of_release as yearOfRelease,
            c.car_mileage as carMileage,
            c.price,
            COALESCE(c.active_driver_id, null) as activeDriverId,
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
            COALESCE(c.active_driver_id, null) as activeDriverId,
            c.brand_id as brandId
        FROM cars c
        LEFT JOIN drivers d ON c.active_driver_id = d.id
        WHERE c.id = :id
        """, nativeQuery = true)
    CarWithBrandIdProjection findCarByIdWithBrandId(@Param("id") UUID id);

    @Modifying
    @Query(value = "DELETE FROM car_drivers WHERE car_id = :carId", nativeQuery = true)
    void deleteCarDriversByCarId(@Param("carId") UUID carId);

    @Modifying
    @Query(value = "DELETE FROM cars WHERE id = :carId", nativeQuery = true)
    void deleteCarById(@Param("carId") UUID carId);

    Page<Car> findCarsByEnterpriseId(
             UUID enterpriseId,
            Pageable pageable
    );

    List<Car> findByEnterpriseId(UUID enterpriseId);

    }
