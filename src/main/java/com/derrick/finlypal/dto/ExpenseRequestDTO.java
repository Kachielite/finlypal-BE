package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequestDTO(
        String description,
        BigDecimal amount,
        LocalDate date,
        ExpenseType type,
        @JsonProperty("category_id")
        Long categoryID
) {
}
