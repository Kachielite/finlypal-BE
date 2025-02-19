package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "InsightsMonthlyComparison", description = "Monthly comparison data")
public class InsightsMonthlyComparisonDTO {
  @Schema(description = "Month of the year", example = "January")
  private String month;

  @JsonProperty("total_spend")
  @NotNull(message = "Total spend is required")
  @Schema(description = "Total spend", example = "100.00")
  private BigDecimal totalSpend;

  @Schema(description = "Expense type", example = "EXPENSE")
  private ExpenseType type;
}
