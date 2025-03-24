package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "UsersUpdateRequest", description = "Holds user update information")
public record UsersUpdateRequestDTO(
        @Schema(description = "Name of user", example = "John Doe") String name,
        @JsonProperty("new_password")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "New password of user", example = "newpassword")
        String newPassword,
        @JsonProperty("old_password")
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Schema(description = "Old password of user", example = "password")
        String oldPassword,

        @JsonProperty("currency_id")
        @Schema(description = "Currency id of user", example = "1")
        @Positive(message = "Currency id must be greater than 0")
        Long currencyId
) {
}
