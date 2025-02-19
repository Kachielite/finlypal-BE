package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "InsightsSpendTrends", description = "Holds spend trends data")
public class InsightsSpendTrendsDTO {
  @Schema(description = "Date of the trend", example = "2023-08-01")
  @NotNull(message = "Date is required")
  private LocalDate date;

  @Schema(description = "Amount of the trend", example = "100.00")
  private BigDecimal amount;

  public InsightsSpendTrendsDTO(LocalDate date, BigDecimal amount) {
    this.date = date;
    this.amount = amount != null ? amount : BigDecimal.ZERO;
  }
}
