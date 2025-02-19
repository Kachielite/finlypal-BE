package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Schema(name = "InsightsTopExpenses", description = "Holds information about top expenses")
public class InsightsTopExpensesDTO {
  @Schema(description = "Expense description", example = "Groceries")
  private String description;

  @Schema(description = "Expense amount", example = "100.00")
  private BigDecimal amount;

  @Schema(description = "Expense date", example = "2023-08-01")
  private LocalDate date;
}
