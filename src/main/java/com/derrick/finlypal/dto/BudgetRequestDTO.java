package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(name = "BudgetRequest", description = "Holds information for creating a budget")
public record BudgetRequestDTO(
        @NotEmpty(message = "Amount cannot be empty")
        @Positive(message = "Amount must be positive")
        @Schema(description = "Amount of budget", example = "100.00")
        BigDecimal amount,

        @NotEmpty(message = "Month cannot be empty")
        @Schema(description = "Month of budget", example = "JANUARY")
        @Pattern(regexp = "JANUARY|FEBRUARY|MARCH|APRIL|MAY|JUNE|JULY|AUGUST|SEPTEMBER|OCTOBER|NOVEMBER|DECEMBER",
                message = "Month must be a valid month of the year")
        String month,

        @NotEmpty(message = "Category cannot be empty")
        @Schema(description = "Category of budget", example = "1")
        Integer categoryID
) {
}
