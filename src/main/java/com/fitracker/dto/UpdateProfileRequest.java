package com.fitracker.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {

    @Positive
    private Double weightKg;
    @Positive
    private Integer heightCm;
    @Positive
    private Integer age;

    @Pattern(regexp = "male|female")
    private String sex;

    @Pattern(regexp = "gain|lose")
    private String goal;

}
