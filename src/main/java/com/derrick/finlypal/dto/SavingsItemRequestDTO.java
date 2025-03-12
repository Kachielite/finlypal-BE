package com.derrick.finlypal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(name = "SavingsItemRequest", description = "Holds information about a savings item")
public record SavingsItemRequestDTO(
    @NotNull(message = "Name is required")
        @Schema(description = "Name of savings item", example = "Passport")
        String name,
    @NotNull(message = "Allocated amount is required")
        @Schema(description = "Allocated amount of savings item", example = "100.00")
        BigDecimal allocatedAmount,
    @Schema(description = "Id of savings this saving item belongs to", example = "1")
        @NotNull(message = "Savings id is required")
        Long savingsId) {}
