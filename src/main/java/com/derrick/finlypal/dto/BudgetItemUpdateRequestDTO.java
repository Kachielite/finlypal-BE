package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(name = "BudgetItemRequest", description = "Holds information for creating a budget item")
public record BudgetItemUpdateRequestDTO(
        @Schema(description = "Name of budget item", example = "Groceries")
        @NotNull(message = "Name is required")
        String name,
        @Schema(description = "Budget icon", example = "🛒")
        @NotNull(message = "Icon is required")
        String icon,
        @Schema(description = "Allocated amount of budget item", example = "100.00")
        @NotNull(message = "Allocated amount is required")
        @Positive(message = "Allocated amount must be greater than 0")
        @JsonProperty("allocated_amount")
        BigDecimal allocatedAmount,
        @Schema(description = "Id of budget", example = "1")
        @NotNull(message = "Budget id is required")
        @JsonProperty("budget_id")
        Long budgetId) {
}
