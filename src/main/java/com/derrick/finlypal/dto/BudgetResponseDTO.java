package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BudgetResponseDTO", description = "Budget response")
public class BudgetResponseDTO {

    @Schema(description = "Budget id", example = "1")
    private Long id;

    @Schema(description = "Budget name", example = "Groceries")
    private String name;

    @Schema(description = "Start date of budget", example = "2023-08-01")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @Schema(description = "End date of budget", example = "2023-08-31")
    @JsonProperty("end_date")
    private LocalDate endDate;

    @Schema(description = "Total budget amount", example = "1000.00")
    @JsonProperty("total_budget")
    private BigDecimal totalBudget;

    @Schema(description = "Budget items belonging to this budget", example = "[]")
    @JsonProperty("budget_items")
    private List<BudgetItemResponseDTO> budgetItems;

    @Schema(description = "Budget status", example = "ACTIVE")
    private String status;

    @Schema(description = "createdAt of budget", example = "2023-08-01")
    @JsonProperty("created_at")
    private LocalDate createdAt;
}
