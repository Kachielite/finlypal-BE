package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.BudgetItemStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BudgetItem", description = "Holds information about a budget item")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetItemResponseDTO {
  @Schema(description = "Id of budget item", example = "1")
  private Long id;

  @Schema(description = "Name of budget item", example = "Groceries")
  private String name;

  @Schema(description = "Allocated amount of budget item", example = "100.00")
  @JsonProperty("allocated_amount")
  private BigDecimal allocatedAmount;

  @Schema(description = "Actual spend of budget item", example = "50.00")
  @JsonProperty("actual_spend")
  private BigDecimal actualSpend;

  @Schema(description = "Status of budget item", example = "ACTIVE")
  private BudgetItemStatus status;

  @Schema(description = "Id of budget", example = "1")
  @JsonProperty("budget_id")
  private Long budgetId;

  @Schema(description = "Expenses of budget item", example = "[]")
  private List<ExpenseResponseDTO> expenses;

  @Schema(description = "createdAt of budget item", example = "2023-08-01")
  @JsonProperty("created_at")
  private LocalDate createdAt;
}
