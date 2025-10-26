package com.fitracker.repository;

import com.fitracker.entity.PlanDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanDayRepository extends JpaRepository<PlanDay, UUID> {
    @Query("SELECT d.id FROM plan_days d WHERE d.plan.id = :planId AND d.date = :date")
    Optional<UUID> findIdByPlanIdAndDate(UUID planId, LocalDate date);

}
