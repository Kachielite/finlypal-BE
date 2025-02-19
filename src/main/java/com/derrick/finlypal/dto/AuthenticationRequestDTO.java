package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AuthenticationRequest", description = "Holds information for user authentication")
public record AuthenticationRequestDTO(
    @Schema(description = "Email of user", example = "D6tYt@example.com")
        @NotBlank(message = "Email cannot be empty")
        String email,
    @Schema(description = "Password of user", example = "password")
        @NotBlank(message = "Password can not be blank")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password) {}
