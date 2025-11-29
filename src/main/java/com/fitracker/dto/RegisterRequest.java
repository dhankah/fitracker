package com.fitracker.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @Size(min = 8)
    private String password;

    @NotNull
    @Positive
    private Double weightKg;

    @NotNull
    @Positive
    private Integer heightCm;

    @NotNull
    @Positive
    private Integer age;

    @Pattern(regexp = "male|female")
    private String sex;

    @Pattern(regexp = "gain|lose")
    private String goal;

}
