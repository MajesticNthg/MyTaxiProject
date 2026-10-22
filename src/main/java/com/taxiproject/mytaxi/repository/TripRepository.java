package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    @Query("""
        SELECT t FROM Trip t
        WHERE t.car.id = :carId
        AND t.startTime >= :from
        AND t.endTime <= :to
        ORDER BY t.startTime ASC
        """)
    List<Trip> findByCarAndPeriod(
            @Param("carId") UUID carId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
