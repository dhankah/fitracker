package com.fitracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionExerciseDto {
    private UUID sessionExerciseId;
    private String exerciseId;
    private String name;
    private String muscleGroup;
    private int sets;
    private int reps;
    private double weightKg;
    private int estimatedCalories;
    private String status;
}
