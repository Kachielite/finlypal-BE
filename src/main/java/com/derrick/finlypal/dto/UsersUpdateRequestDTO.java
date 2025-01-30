package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record UsersUpdateRequestDTO(
        String name,
        @JsonProperty("new_password")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword,
        @JsonProperty("old_password")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String oldPassword
) {
}
