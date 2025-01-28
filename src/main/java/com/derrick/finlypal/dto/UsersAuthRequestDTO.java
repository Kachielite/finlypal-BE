package com.derrick.finlypal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsersAuthRequestDTO(
        @NotBlank(message = "Email cannot be empty")
        String email,
        @NotBlank(message = "Password can not be blank")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
