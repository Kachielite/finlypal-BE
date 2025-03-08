package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExpenseResponse", description = "Response object for an expense")
public class ExpenseResponseDTO {

  @Schema(description = "Id of expense", example = "1")
  private Long id;

  @Schema(description = "Description of expense", example = "Groceries")
  private String description;

  @Schema(description = "Amount of expense", example = "100.00")
  private BigDecimal amount;

  @Schema(description = "Date of expense", example = "2023-08-01")
  private LocalDate date;

  @Schema(description = "Type of expense", example = "EXPENSE")
  private ExpenseType type;

  @Schema(description = "Id of category", example = "1")
  @JsonProperty("category_id")
  private Long categoryId;

  @Schema(description = "category name of expense", example = "Utility")
  @JsonProperty("category_name")
  private String categoryName;
}
