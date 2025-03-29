package com.derrick.finlypal.dto;

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
@Schema(name = "InsightsBudgetOrSavingsMonthlyTrend", description = "Insights budget or savings monthly trend")
public class InsightsBudgetOrSavingsMonthlyTrendDTO {
    @Schema(description = "Month", example = "January")
    private String month;
    @Schema(description = "Amount", example = "1000.00")
    private BigDecimal amount;
}
