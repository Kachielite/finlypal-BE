package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "BudgetResponse", description = "Holds information about a budget")
public class BudgetResponseDTO {

    @Schema(description = "ID of the budget", example = "1")
    private Long id;

    @Schema(description = "Amount of the budget", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Month of the budget", example = "JANUARY")
    private String month;

    @Schema(description = "Category of the budget", example = "1")
    private Long categoryID;
}
