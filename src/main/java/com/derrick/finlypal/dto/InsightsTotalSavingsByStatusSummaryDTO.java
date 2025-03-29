package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.SavingsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(name = "InsightsTotalSavingsByStatus", description = "Holds information about total savings by status")
public class InsightsTotalSavingsByStatusSummaryDTO {
    @Schema(description = "Status of savings", example = "ACTIVE")
    private SavingsStatus status;
    @Schema(description = "Count of savings by status", example = "2")
    private Long count;
}
