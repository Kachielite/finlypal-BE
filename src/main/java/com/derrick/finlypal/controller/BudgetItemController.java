package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.BudgetItemCreateRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.BudgetItemUpdateRequestDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.BudgetItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
@RequestMapping("/budget-item")
@Tag(
    name = "Budget Item",
    description =
        "Budget Item API. This API is responsible for managing budget items. "
            + "A budget item represents an expense or income that you want to track and manage. "
            + "You can create, update, and delete budget items, and also retrieve a list of all budget items for a given budget.")
public class BudgetItemController {
  private final BudgetItemService budgetItemService;

  @GetMapping("/{budget_id}/items")
  @Operation(
      summary = "Get all budget items for a budget",
      description = "This API returns a list of all budget items for a given budget.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget items fetched successfully"),
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
  public ResponseEntity<Page<BudgetItemResponseDTO>> getBudgetItems(
      @PathVariable @NotNull(message = "budgetId cannot be null") Long budget_id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize)
      throws InternalServerErrorException, NotFoundException, NotAuthorizedException {
    return new ResponseEntity<>(
        budgetItemService.getBudgetItems(budget_id, page, pageSize), HttpStatus.OK);
  }

  @GetMapping("/{budget_item_id}")
  @Operation(
      summary = "Get a budget item by ID",
      description = "This API returns a budget item with the specified ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget item fetched successfully"),
    @ApiResponse(
        responseCode = "404",
        description = "Budget item not found",
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
  public ResponseEntity<BudgetItemResponseDTO> getBudgetItem(
      @PathVariable @NotNull(message = "budget_item_id cannot be null") Long budget_item_id)
      throws InternalServerErrorException, NotFoundException, NotAuthorizedException {
    return new ResponseEntity<>(budgetItemService.getBudgetItemById(budget_item_id), HttpStatus.OK);
  }

  @PostMapping("/{budget_id}/items")
  @Operation(
      summary = "Create budget items",
      description = "This API creates multiple budget items for a given budget.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Budget items created successfully"),
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
  public ResponseEntity<GeneralResponseDTO> createBudgetItem(
      @RequestBody List<BudgetItemCreateRequestDTO> budgetItems,
      @PathVariable @NotNull(message = "budget_id cannot be null") Long budget_id)
      throws BadRequestException,
          InternalServerErrorException,
          NotFoundException,
          NotAuthorizedException {
    return new ResponseEntity<>(
        budgetItemService.createBudgetItems(budgetItems, budget_id), HttpStatus.CREATED);
  }

  @PutMapping("/{budget_item_id}")
  @Operation(
      summary = "Update a budget item",
      description = "This API updates a budget item with the specified ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget item updated successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Budget item not found",
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
  public ResponseEntity<BudgetItemResponseDTO> updateBudgetItem(
      @PathVariable @NotNull(message = "budget_item_id cannot be null") Long budget_item_id,
      @RequestBody BudgetItemUpdateRequestDTO budgetItemRequestDTO)
      throws BadRequestException,
          InternalServerErrorException,
          NotFoundException,
          NotAuthorizedException {
    return new ResponseEntity<>(
        budgetItemService.updateBudgetItem(budget_item_id, budgetItemRequestDTO), HttpStatus.OK);
  }

  @DeleteMapping("/{budget_item_id}")
  @Operation(
      summary = "Delete a budget item",
      description = "This API deletes a budget item with the specified ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Budget item deleted successfully"),
    @ApiResponse(
        responseCode = "404",
        description = "Budget item not found",
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
  public ResponseEntity<GeneralResponseDTO> deleteBudgetItem(
      @PathVariable @NotNull(message = "budget_item_id cannot be null") Long budget_item_id)
      throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
    return new ResponseEntity<>(budgetItemService.deleteBudgetItem(budget_item_id), HttpStatus.OK);
  }
}
