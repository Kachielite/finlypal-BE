package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "ExpenseRequest", description = "Holds the data required to create an expense")
public record ExpenseRequestDTO(
    @Schema(description = "Description of expense", example = "Groceries") String description,
    @Schema(description = "Amount of expense", example = "100.00") BigDecimal amount,
    @Schema(description = "Date of expense", example = "2023-08-01") LocalDate date,
    @Schema(description = "Type of expense", example = "EXPENSE") ExpenseType type,
    @Schema(description = "Id of category", example = "1") @JsonProperty("category_id")
        Long categoryID) {}
