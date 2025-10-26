package com.fitracker.repository;

import com.fitracker.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findByMuscleGroupIn(List<String> groups);
}
