package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.CurrencyResponseDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currencies")
@Tag(name = "Currency", description = "Endpoints for managing currencies. " +
        "Currencies are used to determine the denomination of budget items and expenses. " +
        "The API allows you to retrieve the list of all currencies, and to retrieve a " +
        "specific currency by its code.")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping
    @SecurityRequirements()
    @Operation(summary = "Get all currencies", description = "This API returns a list of all currencies.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Currencies retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<CurrencyResponseDTO>> getAllCurrencies() throws InternalServerErrorException {
        return new ResponseEntity<>(currencyService.findAll(), HttpStatus.OK);
    }
}
