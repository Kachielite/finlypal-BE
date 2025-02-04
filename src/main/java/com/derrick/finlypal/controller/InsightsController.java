package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.service.InsightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
@Tag(name = "Insights", description = "Get insights on user spend habits")
public class InsightsController {

    private final InsightsService insightsService;

    @GetMapping("/total_spend")
    @Operation(summary = "Total Spend", description = "Get users total spend")
    public ResponseEntity<ApiResponseDTO<?>> totalSpend(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched total spend",
                            insightsService.getTotalSpend(start_date, end_date, type)
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

    @GetMapping("/total_spend_by_category")
    @Operation(summary = "Total Spend By Category", description = "Get users' total spend by category")
    public ResponseEntity<ApiResponseDTO<?>> totalSpendByCategory(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date,
            @RequestParam ExpenseType type
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched total spend by category",
                            insightsService.getSpendByCategory(start_date, end_date, type)
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

    @GetMapping("/daily_spend")
    @Operation(summary = "Daily Spend", description = "Get users' daily spend")
    public ResponseEntity<ApiResponseDTO<?>> dailySpend(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched daily spend",
                            insightsService.getDailyTrend(start_date, end_date, type)
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

    @GetMapping("/top_expenses")
    @Operation(summary = "Top Expenses", description = "Get users top expenses")
    public ResponseEntity<ApiResponseDTO<?>> topExpenses(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched top expenses",
                            insightsService.getTopExpenses(start_date, end_date, type, page, pageSize)
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

    @GetMapping("/monthly_spend")
    @Operation(summary = "Monthly Spend", description = "Get users monthly spend")
    public ResponseEntity<ApiResponseDTO<?>> monthlySpend(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            HttpStatus.OK.value(),
                            "Successfully fetched monthly spend",
                            insightsService.getMonthlyComparison(start_date, end_date, type)
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


}
