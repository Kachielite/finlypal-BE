package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.dto.ExpenseRequestDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.ExpenseService;
import com.derrick.finlypal.util.InputValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
@Tag(name = "Expenses", description = "Manage user's expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final InputValidation inputValidation;

    @GetMapping("/id/{expense_id}")
    @Operation(summary = "Get Expense", description = "Fetch expense by id")
    public ResponseEntity<ApiResponseDTO<?>> getExpenseById(@PathVariable Long expense_id) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched expense with id:" + expense_id,
                            expenseService.findById(expense_id)
                    ),
                    HttpStatus.OK
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.NOT_FOUND
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @GetMapping
    @Operation(summary = "Get All Expenses", description = "Fetch all logged-in user's expenses")
    public ResponseEntity<ApiResponseDTO<?>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successful fetched user expenses",
                            expenseService.findAllByUserId(page, pageSize)
                    ),
                    HttpStatus.OK
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/category/{category_id}")
    @Operation(summary = "Get expense by category", description = "Fetch expenses by category id for the logged-in user")
    public ResponseEntity<ApiResponseDTO<?>> getExpensesByCategoryId(
            @PathVariable Long category_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched expenses for category with id:" + category_id,
                            expenseService.findAllByCategoryIdAndUserId(category_id, page, pageSize)
                    ),
                    HttpStatus.OK
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/dates")
    @Operation(summary = "Get expense by category", description = "Fetch expenses by category id for the logged-in user")
    public ResponseEntity<ApiResponseDTO<?>> getExpensesByCategoryId(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched expenses",
                            expenseService.findAllByDateBetween(start_date, end_date, page, pageSize)
                    ),
                    HttpStatus.OK
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/types/{type}")
    @Operation(summary = "Get expense by type", description = "Fetch expenses by expense type and dates for the logged-in user")
    public ResponseEntity<ApiResponseDTO<?>> getExpensesByTypeAndDate(
            @PathVariable ExpenseType type,
            @RequestParam(required = false) Optional<LocalDate> start_date,
            @RequestParam(required = false) Optional<LocalDate> end_date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched expenses",
                            expenseService.findAllByTypeAndUserIdOrDateBetween(
                                    type,
                                    start_date.orElse(null),
                                    end_date.orElse(null),
                                    page,
                                    pageSize
                            )
                    ),
                    HttpStatus.OK
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping
    @Operation(summary = "Add expense", description = "Create new expense")
    public ResponseEntity<ApiResponseDTO<?>> createExpense(
            @Valid @RequestBody ExpenseRequestDTO expense,
            BindingResult bindingResult
    ) {
        try {
            // Validate Request Body
            Map<String, String> errors = inputValidation.validate(bindingResult);
            if (errors != null && !errors.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponseDTO<>(400, "Validation Error", errors),
                        HttpStatus.BAD_REQUEST
                );
            }
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.CREATED.value(),
                            "Expense added successfully",
                            expenseService.addExpense(expense)
                    ),
                    HttpStatus.CREATED
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PutMapping("/{expense_id}")
    @Operation(summary = "Update expense", description = "Modify exisiting expense")
    public ResponseEntity<ApiResponseDTO<?>> updateExpense(
            @PathVariable Long expense_id,
            @RequestBody ExpenseRequestDTO expense
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully updated expense with id:" + expense_id,
                            expenseService.updateExpense(expense_id, expense)
                    ),
                    HttpStatus.OK
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.NOT_FOUND
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @DeleteMapping("/{expense_id}")
    @Operation(summary = "Delete expense", description = "Delete expense by id")
    public ResponseEntity<ApiResponseDTO<?>> deleteExpense(@PathVariable Long expense_id) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully deleted expense with id:" + expense_id,
                            expenseService.deleteExpense(expense_id)
                    ),
                    HttpStatus.OK
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.NOT_FOUND.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.NOT_FOUND
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.UNAUTHORIZED.value(),
                            e.getMessage(),
                            null
                    ),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

}
