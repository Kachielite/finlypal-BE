package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDTO extends ApiResponseDTO<ExpenseResponseDTO> {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private ExpenseType type;
    private Long category_id;
}
