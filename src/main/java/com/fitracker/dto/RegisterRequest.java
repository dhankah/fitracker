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
    private Double weightKg;

    @NotNull
    private Integer heightCm;

    @NotNull
    private Integer age;

    @Pattern(regexp = "male|female")
    private String sex;

    @Pattern(regexp = "gain|lose")
    private String goal;

}
