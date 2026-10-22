package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Driver;
import com.taxiproject.mytaxi.projection.DriversIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    @Query(value = """
        SELECT 
            d.id,
            d.name,
            d.age,
            d.salary,
            d.enterprise_id as enterpriseId
        FROM drivers d
        """, nativeQuery = true)
    List<DriversIdProjection> findAllDriversWithEnterpriseId();

    @Query(value = """
        SELECT 
            d.id,
            d.name,
            d.age,
            d.salary,
            d.enterprise_id as enterpriseId
        FROM drivers d
        WHERE d.id = :id
        """, nativeQuery = true)
    DriversIdProjection findDriverByIdWithEnterpriseId(@Param("id") UUID id);

    List<Driver> findAllByIdIn(List<UUID> ids);
}
