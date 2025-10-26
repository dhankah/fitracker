package com.fitracker.repository;

import com.fitracker.entity.Coefficient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoefficientRepository extends JpaRepository<Coefficient, UUID> {
    Optional<Coefficient> findByUserIdAndMuscleGroup(UUID userId, String muscleGroup);
}
