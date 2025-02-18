package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "UsersUpdateRequest", description = "Holds user update information")
public record UsersUpdateRequestDTO(
        @Schema(description = "Name of user", example = "John Doe")
        String name,
        @JsonProperty("new_password")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "New password of user", example = "newpassword")
        String newPassword,
        @JsonProperty("old_password")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "Old password of user", example = "password")
        String oldPassword
) {
}
