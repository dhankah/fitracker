package com.fitracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "session_exercises")
public class SessionExercise {

    @Id
    private UUID id;

    @Column(name = "plan_day_id", nullable = false)
    private UUID planDayId;

    @ManyToOne
    @JoinColumn(name = "plan_day_id", insertable=false, updatable=false)
    private PlanDay day;

    @Column(name = "exercise_id", nullable = false)
    private String exerciseId;

    private int sets;
    private int reps;

    @Column(name = "weight_kg")
    private double weightKg;

    @Column(name = "estimated_calories")
    private int estimatedCalories;

    private String status;
}
