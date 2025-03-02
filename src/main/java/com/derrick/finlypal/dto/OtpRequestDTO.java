package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "OtpRequest", description = "Holds information for OTP request")
public record OtpRequestDTO(
    @Email(message = "Invalid email address")
        @NotBlank(message = "Email cannot be empty")
        @Schema(description = "Email address of the user", example = "D6tYt@example.com")
        String email,
    @Schema(description = "OTP code", example = "123456")
        @Min(value = 1000, message = "OTP must be a 4-digit number")
        @Max(value = 9999, message = "OTP must be a 4-digit number")
        Integer otp) {}
