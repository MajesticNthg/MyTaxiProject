package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("""
        SELECT t FROM Trip t
        WHERE t.car.id = :carId
        AND t.startTime >= :from
        AND t.endTime <= :to
        ORDER BY t.startTime ASC
        """)
    List<Trip> findByCarAndPeriod(
            @Param("carId") Integer carId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
