package com.derrick.finlypal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsersRegistrationRequestDTO(
        @NotBlank(message = "Name can not be blank")
        String name,
        @NotBlank(message = "Email can not be blank")
        @Email(message = "Invalid email")
        String email,
        @NotBlank(message = "Password can not be blank")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
