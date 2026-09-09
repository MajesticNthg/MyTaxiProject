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

@Repository
public interface GpsPointRepository extends JpaRepository<GpsPoint, Long> {

    Page<GpsPoint> findByCarIdAndTimestampBetween(
            Integer carId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    List<GpsPoint> findByCarIdAndTimestampBetweenOrderByTimestampAsc(
            Integer carId,
            LocalDateTime from,
            LocalDateTime to
    );

    GpsPoint findTopByCarIdOrderByTimestampDesc(Integer carId);
}
