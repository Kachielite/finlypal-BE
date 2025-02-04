package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightsSpendByCategoryDTO {
    private String category;

    @JsonProperty("total_spend")
    @NotNull(message = "Amount is required")
    private BigDecimal totalSpend;

    private Integer percentage;
}
