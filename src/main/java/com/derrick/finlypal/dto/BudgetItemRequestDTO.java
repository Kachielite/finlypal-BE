package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.BudgetItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "BudgetItemRequest", description = "Holds information for creating a budget item")
public record BudgetItemRequestDTO(
        @Schema(description = "Name of budget item", example = "Groceries")
        String name,
        @Schema(description = "Allocated amount of budget item", example = "100.00")
        BigDecimal allocatedAmount,
        @Schema(description = "Status of budget item", example = "ACTIVE")
        BudgetItemStatus status,
        @Schema(description = "Id of budget", example = "1")
        Long budgetId
) {
}
