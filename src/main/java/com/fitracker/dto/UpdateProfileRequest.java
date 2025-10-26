package com.fitracker.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {

    private Double weightKg;
    private Integer heightCm;
    private Integer age;

    @Pattern(regexp = "male|female")
    private String sex;

    @Pattern(regexp = "gain|lose")
    private String goal;

}
