package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.BudgetItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "BudgetItem", description = "Holds information about a budget item")
public class BudgetItemResponse {
    @Schema(description = "Id of budget item", example = "1")
    private Long id;
    @Schema(description = "Name of budget item", example = "Groceries")
    private String name;
    @Schema(description = "Allocated amount of budget item", example = "100.00")
    private BigDecimal allocatedAmount;
    @Schema(description = "Status of budget item", example = "ACTIVE")
    private BudgetItemStatus status;
    @Schema(description = "Id of budget", example = "1")
    private Long budgetId;
    @Schema(description = "createdAt of budget item", example = "2023-08-01")
    private Timestamp createdAt;
}
