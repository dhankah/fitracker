package com.fitracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String email;
    private Double weightKg;
    private Integer heightCm;
    private Integer age;
    private String sex;
    private String goal;
}
