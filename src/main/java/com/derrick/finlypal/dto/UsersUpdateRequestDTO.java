package com.derrick.finlypal.dto;

import jakarta.validation.constraints.Size;

public record UsersUpdateRequestDTO(
        String name,
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword,
        @Size(min = 6, message = "Password must be at least 6 characters")
        String oldPassword
) {
}
