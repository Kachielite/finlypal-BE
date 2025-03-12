package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "ExpenseRequest", description = "Holds the data required to create an expense")
public record ExpenseRequestDTO(
    @Schema(description = "Description of expense", example = "Groceries")
        @NotNull(message = "Description is required")
        String description,
    @Schema(description = "Amount of expense", example = "100.00")
        @NotNull(message = "Amount is required")
        @PositiveOrZero(message = "Amount must be greater than or equal to zero")
        BigDecimal amount,
    @Schema(description = "Date of expense", example = "2023-08-01")
        @NotNull(message = "Date is required")
        LocalDate date,
    @Schema(description = "Type of expense", example = "EXPENSE")
        @NotNull(message = "Type is required")
        @Pattern(regexp = "^(EXPENSE|INCOME)$", message = "Type must be either EXPENSE or INCOME")
        ExpenseType type,
    @Schema(description = "Id of category", example = "1") @JsonProperty("category_id")
        Long categoryID,
    @Schema(description = "Id of savings item this expense belongs to", example = "1")
        @JsonProperty("savings_item_id")
        Long savingsItemID,
    @Schema(description = "Id of budget item this expense belongs to", example = "1")
        @JsonProperty("budget_item_id")
        Long budgetItemID) {}
