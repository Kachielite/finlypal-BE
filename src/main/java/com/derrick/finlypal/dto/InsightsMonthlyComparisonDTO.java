package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightsMonthlyComparisonDTO {
    private String month;

    @JsonProperty("total_spend")
    @NotNull(message = "Total spend is required")
    private BigDecimal totalSpend;

    private ExpenseType type;
    
}
