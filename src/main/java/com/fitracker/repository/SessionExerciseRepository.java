package com.fitracker.repository;

import com.fitracker.entity.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
    public interface SessionExerciseRepository extends JpaRepository<SessionExercise, UUID> {
        List<SessionExercise> findByPlanDayId(UUID planDayId);
    }
