package com.taxiproject.mytaxi.repository;

import com.taxiproject.mytaxi.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    Optional<Manager> findByUsername(String username);

    @Query(value = """
        SELECT
            JSON_BUILD_OBJECT(
            'id', m.id,
            'username', m.username,
            'fullName', m.full_name,
            'email', m.email,
            'enterprises', COALESCE(
                (SELECT JSON_AGG(
                    JSON_BUILD_OBJECT(
                        'id', e.id,
                        'name', e.name,
                        'cars', COALESCE(
                            (SELECT JSON_AGG(
                                JSON_BUILD_OBJECT(
                                    'id', c.id,
                                    'licensePlate', c.license_plate
                                ))
                            FROM cars c WHERE c.enterprise_id = e.id),
                            '[]'::json
                        ),
                        'drivers', COALESCE(
                            (SELECT JSON_AGG(
                                JSON_BUILD_OBJECT(
                                    'id', d.id,
                                    'name', d.name
                                ))
                            FROM drivers d WHERE d.enterprise_id = e.id),
                            '[]'::json
                        )
                    )
                ) FROM manager_enterprises me
                JOIN enterprises e ON me.enterprise_id = e.id
                WHERE me.manager_id = m.id),
                '[]'::json
            )
        ) as TEXT
    FROM managers m
    WHERE m.id = :id
    """, nativeQuery = true)
String findManagerWithEnterprisesById(@Param("id") Integer id);
}
