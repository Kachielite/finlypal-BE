package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "SavingsRequest", description = "Holds information for creating a savings goal")
public record SavingsRequestDTO(
        @Schema(description = "Name of savings goal", example = "Travel")
        @NotNull(message = "Goal name is required")
        String goalName,

        @Schema(description = "Target amount of savings goal", example = "1000.00")
        @NotNull(message = "Target amount is required")
        BigDecimal targetAmount,

        @Schema(description = "Saved amount of savings goal", example = "500.00")
        @NotNull(message = "Saved amount is required")
        BigDecimal savedAmount,

        @Schema(description = "Start date of savings goal", example = "2023-08-01")
        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @Schema(description = "End date of savings goal", example = "2023-08-31")
        @NotNull(message = "End date is required")
        LocalDate endDate
) {
}
