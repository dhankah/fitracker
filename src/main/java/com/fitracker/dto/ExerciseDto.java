package com.fitracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private String exerciseId;
    private String name;
    private String muscleGroup;
    private int sets;
    private int reps;
    private double weightKg;
    private int estimatedCalories;
}
