package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.BudgetRequestDTO;
import com.derrick.finlypal.dto.BudgetResponseDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.BudgetService;
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
@RequiredArgsConstructor
@RequestMapping("/budget")
@Tag(
    name = "Budget",
    description =
        "This API is used to manage budgets. A budget is a financial plan for a particular period of time. It can be used to track income, expenses and savings. Budgets can be created, updated and deleted. Budgets can also be retrieved by id or by a list of ids. Budgets can also be retrieved in a paginated fashion by providing a page number and a page size.")
public class BudgetController {

  private final BudgetService budgetService;

  @PostMapping("/")
  @Operation(
      summary = "Create a new budget",
      description =
          "This operation creates a new budget for the logged-in user. It validates the provided budget details, ensures the user is authenticated, and saves the budget in the database. The response includes the budget's ID, name, start and end dates, total budget amount, status, and creation date.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Budget created"),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<BudgetResponseDTO> createBudget(
      @Valid @RequestBody BudgetRequestDTO budgetRequestDTO)
      throws BadRequestException, InternalServerErrorException {
    return new ResponseEntity<>(budgetService.createBudget(budgetRequestDTO), HttpStatus.CREATED);
  }

  @PutMapping("/{budget_id}")
  @Operation(
      summary = "Update a budget",
      description =
          "This operation updates a budget for the logged-in user. It validates the provided budget details, ensures the user is authenticated, and ensures the budget exists and belongs to the logged-in user. The response includes the budget's ID, name, start and end dates, total budget amount, status, and creation date.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget updated"),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Budget not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Not authorized",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<BudgetResponseDTO> updateBudget(
      @PathVariable @NotNull(message = "budget_id cannot be null") Long budget_id,
      @Valid @RequestBody BudgetRequestDTO budgetRequestDTO)
      throws BadRequestException,
          NotFoundException,
          NotAuthorizedException,
          InternalServerErrorException {
    return new ResponseEntity<>(
        budgetService.updateBudget(budget_id, budgetRequestDTO), HttpStatus.OK);
  }

  @GetMapping("/")
  @Operation(
      summary = "Get all budgets",
      description =
          "This operation returns a list of all budgets for the logged-in user. It validates the user is authenticated and returns a paginated list of budgets.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budgets fetched successfully"),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<Page<BudgetResponseDTO>> getAllBudgets(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize)
      throws InternalServerErrorException {
    return new ResponseEntity<>(budgetService.getAllBudgets(page, pageSize), HttpStatus.OK);
  }

  @GetMapping("/{budget_id}")
  @Operation(
      summary = "Get a budget by ID",
      description =
          "This operation returns a budget for the logged-in user. It validates the user is authenticated and returns a paginated list of budgets.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget fetched successfully"),
    @ApiResponse(
        responseCode = "404",
        description = "Budget not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<BudgetResponseDTO> getBudgetById(
      @PathVariable @NotNull(message = "budget_id cannot be null") Long budget_id)
      throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
    return new ResponseEntity<>(budgetService.getBudgetById(budget_id), HttpStatus.OK);
  }

  @DeleteMapping("/{budget_id}")
  @Operation(
      summary = "Delete a budget",
      description =
          "This operation deletes a budget for the logged-in user. It validates the user is authenticated and ensures the budget exists and belongs to the logged-in user.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget deleted"),
    @ApiResponse(
        responseCode = "404",
        description = "Budget not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Not authorized",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<GeneralResponseDTO> deleteBudget(
      @PathVariable @NotNull(message = "budget_id cannot be null") Long budget_id)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
    return new ResponseEntity<>(budgetService.deleteBudget(budget_id), HttpStatus.OK);
  }
}
