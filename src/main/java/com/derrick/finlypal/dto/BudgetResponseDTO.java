package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
@Schema(name = "BudgetResponseDTO", description = "Budget response")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BudgetResponseDTO {

  @Schema(description = "Budget id", example = "1")
  private Long id;

  @Schema(description = "Budget name", example = "Groceries")
  private String name;

  @Schema(description = "Icon of budget item", example = "🛒")
  private String icon;

  @Schema(description = "Start date of budget", example = "2023-08-01")
  @JsonProperty("start_date")
  private LocalDate startDate;

  @Schema(description = "End date of budget", example = "2023-08-31")
  @JsonProperty("end_date")
  private LocalDate endDate;

  @Schema(description = "Total budget amount", example = "1000.00")
  @JsonProperty("total_budget")
  private BigDecimal totalBudget;

  @Schema(description = "Actual spend of budget", example = "50.00")
  @JsonProperty("actual_spend")
  private BigDecimal actualSpend;

  @Schema(description = "Budget items belonging to this budget", example = "[]")
  @JsonProperty("budget_items")
  private List<BudgetItemResponseDTO> budgetItems;

  @Schema(description = "Budget status", example = "ACTIVE")
  private String status;

  @Schema(description = "Budget status tooltip", example = "Active")
  @JsonProperty("status_tooltip")
  private String statusTooltip;

  @Schema(description = "createdAt of budget", example = "2023-08-01")
  @JsonProperty("created_at")
  private Timestamp createdAt;
}
