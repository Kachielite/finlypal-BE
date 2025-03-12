package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "BudgetRequest", description = "Holds budget information")
public record BudgetRequestDTO(
    @Schema(description = "Budget name", example = "Groceries") @JsonProperty("budget_name")
        String budgetName,
    @Schema(description = "Start date of budget", example = "2023-08-01")
        @JsonProperty("start_date")
        LocalDate startDate,
    @Schema(description = "End date of budget", example = "2023-08-31") @JsonProperty("end_date")
        LocalDate endDate,
    @Schema(description = "Total budget amount", example = "1000.00") @JsonProperty("total_budget")
        BigDecimal totalBudget) {}
