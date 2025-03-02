package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Schema(name = "OtpRequest", description = "Holds information for OTP request")
public record OtpRequestDTO(
        @Email(message = "Invalid email address")
        @NotEmpty(message = "Email cannot be empty")
        @Schema(description = "Email address of the user", example = "D6tYt@example.com")
        String email,
        @NotEmpty(message = "OTP cannot be empty")
        @Schema(description = "OTP code", example = "123456")
        @Pattern(regexp = "^[0-9]{4}$", message = "OTP must be a 6-digit number")
        int otp

) {
}
