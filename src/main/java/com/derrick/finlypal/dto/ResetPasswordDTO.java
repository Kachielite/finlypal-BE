package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ResetPassword", description = "Reset password request")
public record ResetPasswordDTO(
    @NotBlank(message = "Token is required")
        @Schema(
            description = "The token used to authorize the password reset",
            example = "123456789")
        String token,
    @NotBlank(message = "New password is required")
        @Schema(description = "The new password for the user", example = "password")
        @JsonProperty("new_password")
        String newPassword,
    @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Schema(description = "The email of the user", example = "D6tYt@example.com")
        String email) {}
