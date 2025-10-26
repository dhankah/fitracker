package com.fitracker.repository;

import com.fitracker.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    @Query("SELECT p.id FROM plans p WHERE p.userId = :userId AND p.weekStart = :weekStart")
    Optional<UUID> findIdByUserIdAndWeekStart(UUID userId, LocalDate weekStart);

}
