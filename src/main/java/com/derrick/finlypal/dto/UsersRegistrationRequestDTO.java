package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UsersRegistrationRequest", description = "Holds user registration details")
public record UsersRegistrationRequestDTO(
    @Schema(description = "Name of user", example = "John Doe")
        @NotBlank(message = "Name can not be blank")
        String name,
    @Schema(description = "Email of user", example = "D6tYt@example.com")
        @NotBlank(message = "Email can not be blank")
        @Email(message = "Invalid email")
        String email,
    @Schema(description = "Password of user", example = "password")
        @NotBlank(message = "Password can not be blank")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password) {}
