package com.derrick.finlypal.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightsTopExpensesDTO {
    private String description;
    private BigDecimal amount;
    private LocalDate date;
}
