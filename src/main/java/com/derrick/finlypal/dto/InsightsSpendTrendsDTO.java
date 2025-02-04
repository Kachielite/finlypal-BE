package com.derrick.finlypal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class InsightsSpendTrendsDTO {
    @NotNull(message = "Date is required")
    private LocalDate date;

    private BigDecimal amount;

    public InsightsSpendTrendsDTO(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
    }
}
