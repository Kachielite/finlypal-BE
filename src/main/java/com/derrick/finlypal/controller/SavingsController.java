package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.SavingsRequestDTO;
import com.derrick.finlypal.dto.SavingsResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.SavingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savings")
@RequiredArgsConstructor
@Tag(
        name = "Savings",
        description = "Endpoints for managing savings goals, including creation, update, retrieval, and deletion of savings records. These APIs allow users to set financial goals and track their savings progress over time."
)
public class SavingsController {
    private final SavingsService savingsService;

    @PostMapping("/")
    @Operation(
            summary = "Create a new savings goal",
            description = "Create a new savings goal with the provided details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Savings goal created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = BadRequestException.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = NotAuthorizedException.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorException.class))
            )
    })
    public ResponseEntity<SavingsResponseDTO> createSavings(
            @Valid @RequestBody SavingsRequestDTO savingsRequestDTO)
            throws BadRequestException, InternalServerErrorException {
        return new ResponseEntity<>(
                savingsService.createSavings(savingsRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{savings_id}")
    @Operation(
            summary = "Update an existing savings goal",
            description = "Update an existing savings goal with the provided details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Savings goal updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = BadRequestException.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access",
                    content = @Content(schema = @Schema(implementation = NotAuthorizedException.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Savings goal not found",
                    content = @Content(schema = @Schema(implementation = NotFoundException.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorException.class))
            )
    })
    public ResponseEntity<SavingsResponseDTO> updateSavings(
            @PathVariable @NotNull(message = "savings_id cannot be null") Long savings_id,
            @Valid @RequestBody SavingsRequestDTO savingsRequestDTO)
            throws BadRequestException,
            InternalServerErrorException,
            NotFoundException,
            NotAuthorizedException {
        return new ResponseEntity<>(
                savingsService.updateSavings(savings_id, savingsRequestDTO), HttpStatus.OK);
    }

    @GetMapping("/{savings_id}")
    @Operation(
            summary = "Retrieve a savings goal by ID",
            description = "Retrieve a savings goal by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Savings goal retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Savings goal not found",
                    content = @Content(schema = @Schema(implementation = NotFoundException.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorException.class))
            )
    })
    public ResponseEntity<SavingsResponseDTO> getSavingsById(
            @PathVariable @NotNull(message = "savings_id cannot be null") Long savings_id)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
        return new ResponseEntity<>(savingsService.getSavingsById(savings_id), HttpStatus.OK);
    }

    @GetMapping("/")
    @Operation(
            summary = "Retrieve all savings goals",
            description = "Retrieve a list of all savings goals."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Savings goals retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorException.class))
            )
    })
    public ResponseEntity<Page<SavingsResponseDTO>> getAllSavings(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize)
            throws InternalServerErrorException {
        return new ResponseEntity<>(savingsService.getAllSavings(page, pageSize), HttpStatus.OK);
    }

    @DeleteMapping("/{savings_id}")
    @Operation(
            summary = "Delete a savings goal by ID",
            description = "Delete a savings goal by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Savings goal deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Savings goal not found",
                    content = @Content(schema = @Schema(implementation = NotFoundException.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorException.class))
            )
    })
    public ResponseEntity<GeneralResponseDTO> deleteSavings(
            @PathVariable @NotNull(message = "savings_id cannot be null") Long savings_id)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
        return new ResponseEntity<>(savingsService.deleteSavings(savings_id), HttpStatus.OK);
    }
}
