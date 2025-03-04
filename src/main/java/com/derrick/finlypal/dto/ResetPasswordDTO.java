package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ResetPassword", description = "Reset password request")
public record ResetPasswordDTO(
        @Schema(description = "The token used to authorize the password reset", example = "123456789")
        String token,
        @Schema(description = "The new password for the user", example = "password")
        @JsonProperty("new_password")
        String newPassword,
        @Schema(description = "The email of the user", example = "D6tYt@example.com")
        String email
) {
}
