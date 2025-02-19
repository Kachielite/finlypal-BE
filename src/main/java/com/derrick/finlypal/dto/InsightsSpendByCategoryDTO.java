package com.derrick.finlypal.dto;

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
@Schema(
    name = "InsightsSpendByCategory",
    description = "Holds information about the spend by category")
public class InsightsSpendByCategoryDTO {

  @Schema(description = "Category name", example = "Groceries")
  private String category;

  @JsonProperty("total_spend")
  @NotNull(message = "Amount is required")
  @Schema(description = "Total spend", example = "100.00")
  private BigDecimal totalSpend;

  @Schema(description = "Percentage of total spend", example = "50")
  private Integer percentage;
}
