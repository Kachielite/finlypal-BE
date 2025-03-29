package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.BudgetStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(name = "InsightsTotalBudgetByStatusSummary", description = "Holds information about total budget by status")
public class InsightsTotalBudgetByStatusSummaryDTO {
    @Schema(description = "Status of budget", example = "ACTIVE")
    private BudgetStatus status;
    @Schema(description = "Count of budget", example = "1")
    private Long count;
}
