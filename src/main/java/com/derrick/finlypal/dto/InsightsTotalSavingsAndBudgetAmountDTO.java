package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(name = "InsightsTotalSavingsAndBudgetAmount", description = "Insights total savings and budget amount")
public class InsightsTotalSavingsAndBudgetAmountDTO {
    @Schema(description = "Total savings amount", example = "1000.00")
    @JsonProperty("total_savings")
    private BigDecimal totalSavings;
    @Schema(description = "Total budget amount", example = "1000.00")
    @JsonProperty("total_budget")
    private BigDecimal totalBudget;
}
