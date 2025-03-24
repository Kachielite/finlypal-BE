package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "CurrencyResponse", description = "Holds currency information")
public class CurrencyResponseDTO {
    @Schema(description = "Currency code", example = "USD")
    private String code;
    @Schema(description = "Currency name", example = "United States Dollar")
    private String name;
    @Schema(description = "Currency symbol", example = "$")
    private String symbol;
}
