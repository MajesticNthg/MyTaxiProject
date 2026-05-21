package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taxiproject.mytaxi.projection.EnterpriseProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Integer> {
    @Query(value = """
            SELECT 
                id,
                name,
                city,
                employee_count as employeeCount
            FROM enterprises
            """, nativeQuery = true)
    List<EnterpriseProjection> findAllEnterprisesProjection();

    @Query(value = """
            SELECT 
                id,
                name,
                city,
                employee_count as employeeCount
            FROM enterprises
            WHERE id = :id
            """, nativeQuery = true)
    EnterpriseProjection findEnterpriseByIdProjection(@Param("id") Integer id);

    @Query(value = """
                SELECT CAST(
                    JSONB_BUILD_OBJECT(
                        'id', e.id,
                        'name', e.name,
                        'city', e.city,
                        'employeeCount', e.employee_count,
            
                        'cars', COALESCE(
                            (
                                SELECT JSONB_AGG(
                                    JSONB_BUILD_OBJECT(
                                        'id', c.id,
                                        'licensePlate', c.license_plate
                                    )
                                )
                                FROM cars c
                                WHERE c.enterprise_id = e.id
                            ),
                            '[]'::jsonb
                        ),
            
                        'drivers', COALESCE(
                            (
                                SELECT JSONB_AGG(
                                    JSONB_BUILD_OBJECT(
                                        'id', d.id,
                                        'name', d.name
                                    )
                                )
                                FROM drivers d
                                WHERE d.enterprise_id = e.id
                            ),
                            '[]'::jsonb
                        )
                    ) AS TEXT
                )
                FROM enterprises e
                WHERE e.id = :id
            """, nativeQuery = true)
    String findEnterpriseFullJson(@Param("id") Integer id);

}
