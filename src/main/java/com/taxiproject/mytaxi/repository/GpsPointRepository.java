package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.GpsPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GpsPointRepository extends JpaRepository<GpsPoint, UUID> {

    Page<GpsPoint> findByCarIdAndTimestampBetween(
            UUID carId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    List<GpsPoint> findByCarIdAndTimestampBetweenOrderByTimestampAsc(
            UUID carId,
            LocalDateTime from,
            LocalDateTime to
    );

    GpsPoint findTopByCarIdOrderByTimestampDesc(UUID carId);
}
