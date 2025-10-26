package com.fitracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coefficients")
@IdClass(CoefficientId.class)
public class Coefficient {

    @Id
    private UUID userId;

    @Id
    private String muscleGroup;

    private double value;
}
