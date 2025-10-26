package com.fitracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    private String id;
    private String name;
    private String muscleGroup;
    private int baseSets;
    private int baseReps;
    private double baseWeightKg;
    private int baseCalories;
}
