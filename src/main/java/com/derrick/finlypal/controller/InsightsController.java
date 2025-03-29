package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.InsightsBudgetOrSavingsMonthlyTrendDTO;
import com.derrick.finlypal.dto.InsightsMonthlyComparisonDTO;
import com.derrick.finlypal.dto.InsightsSpendByCategoryDTO;
import com.derrick.finlypal.dto.InsightsSpendTrendsDTO;
import com.derrick.finlypal.dto.InsightsTopExpensesDTO;
import com.derrick.finlypal.dto.InsightsTotalBudgetByStatusSummaryDTO;
import com.derrick.finlypal.dto.InsightsTotalSavingsAndBudgetAmountDTO;
import com.derrick.finlypal.dto.InsightsTotalSavingsByStatusSummaryDTO;
import com.derrick.finlypal.dto.InsightsTotalSpendDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.enums.InsightMonthlyTrendType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.service.InsightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
@Tag(
        name = "User Spending Insights",
        description =
                "Provides detailed insights into user spending habits, including total spend and categorization over specified periods.")
public class InsightsController {

    private final InsightsService insightsService;

    @GetMapping("/total-spend")
    @Operation(
            summary = "Total Spend",
            description =
                    """
                            Get users total spend.
                            This endpoint returns the total amount spent by a user
                            within a specified period. The period can be specified
                            using the query parameters start_date and end_date.
                            If the period is not specified, the endpoint will return
                            the total amount spent by the user since the start of
                            the current month.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total spend fetched successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<InsightsTotalSpendDTO> totalSpend(
            @RequestParam(required = true) LocalDate start_date,
            @RequestParam(required = true) LocalDate end_date,
            @RequestParam ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        return new ResponseEntity<>(
                insightsService.getTotalSpend(start_date, end_date, type), HttpStatus.OK);
    }

    @GetMapping("/total-spend-by-category")
    @Operation(
            summary = "Total Spend By Category",
            description =
                    """
                            Get users' total spend by category.
                            This endpoint returns the total amount spent by a user
                            in each category within a specified period. The period
                            can be specified using the query parameters start_date
                            and end_date. If the period is not specified, the
                            endpoint will return the total amount spent by the user
                            since the start of the current month. The categories are
                            ordered by their total spend in descending order.
                            """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Total spend by category fetched successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<InsightsSpendByCategoryDTO>> totalSpendByCategory(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date,
            @RequestParam ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        return new ResponseEntity<>(
                insightsService.getSpendByCategory(start_date, end_date, type), HttpStatus.OK);
    }

    @GetMapping("/daily-spend")
    @Operation(
            summary = "User's Daily Spending Analysis",
            description =
                    """
                            Retrieve a detailed analysis of the user's daily spending habits.
                            This endpoint provides a day-by-day breakdown of the total amount
                            spent by the user within a specified date range. If no date range
                            is provided, the default will be the current month. The data is
                            useful for understanding spending patterns and making informed
                            financial decisions.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Daily spend fetched successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<InsightsSpendTrendsDTO>> dailySpend(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        return new ResponseEntity<>(
                insightsService.getDailyTrend(start_date, end_date, type), HttpStatus.OK);
    }

    @GetMapping("/top-expenses")
    @Operation(
            summary = "Top Expenses",
            description =
                    """
                            Get users top expenses.
                            This endpoint returns a list of the user's top expenses
                            within a specified period. The period can be specified
                            using the query parameters start_date and end_date. If
                            the period is not specified, the endpoint will return the
                            top expenses for the current month. The expenses are
                            ordered by their amount in descending order.
                            """)
    public ResponseEntity<Page<InsightsTopExpensesDTO>> topExpenses(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize)
            throws InternalServerErrorException, BadRequestException {
        return new ResponseEntity<>(
                insightsService.getTopExpenses(start_date, end_date, type, page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/monthly-spend")
    @Operation(
            summary = "Monthly Spend",
            description =
                    """
                            Get users monthly spend.
                            This endpoint returns the total spend for the user for each month
                            within a specified period. The period can be specified using the query
                            parameters start_date and end_date. If the period is not specified,
                            the endpoint will return the monthly spend for the current month.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Monthly spend fetched successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<InsightsMonthlyComparisonDTO>> monthlySpend(
            @RequestParam(required = false) LocalDate start_date,
            @RequestParam(required = false) LocalDate end_date,
            @RequestParam ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        return new ResponseEntity<>(
                insightsService.getMonthlyComparison(start_date, end_date, type), HttpStatus.OK);
    }


    @GetMapping("/total-savings-and-budget-amount")
    @Operation(
            summary = "Total Savings and Budget Amount",
            description =
                    """
                            Get users total savings and budget amount.
                            This endpoint returns the total savings and budget amount for the user
                            within a specified period.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    }
    )
    public ResponseEntity<InsightsTotalSavingsAndBudgetAmountDTO> totalSavingsAndBudgetAmount(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date)
            throws InternalServerErrorException {
        return new ResponseEntity<>(
                insightsService.getTotalSavingsAndBudgetAmount(start_date, end_date), HttpStatus.OK
        );
    }


    @GetMapping("/total-budget-by-status-summary")
    @Operation(
            summary = "Total Budget By Status Summary",
            description =
                    """
                            Get users total budget by status summary.
                            This endpoint returns the total budget by status summary for the user
                            within a specified period.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    }
    )
    public ResponseEntity<List<InsightsTotalBudgetByStatusSummaryDTO>> totalBudgetByStatusSummary(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date)
            throws InternalServerErrorException {
        return new ResponseEntity<>(
                insightsService.getTotalBudgetByStatusSummary(start_date, end_date), HttpStatus.OK
        );
    }


    @GetMapping("/total-savings-by-status-summary")
    @Operation(
            summary = "Total Savings By Status Summary",
            description =
                    """
                            Get users total savings by status summary.
                            This endpoint returns the total savings by status summary for the user
                            within a specified period.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    }
    )
    public ResponseEntity<List<InsightsTotalSavingsByStatusSummaryDTO>> totalSavingsByStatusSummary(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date)
            throws InternalServerErrorException {
        return new ResponseEntity<>(
                insightsService.getTotalSavingsByStatusSummary(start_date, end_date), HttpStatus.OK
        );
    }


    @GetMapping("/budget-or-savings-monthly-trend")
    @Operation(
            summary = "Budget or Savings Monthly Trend",
            description =
                    """
                            Get users budget or savings monthly trend.
                            This endpoint returns the budget or savings monthly trend for the user
                            within a specified period.
                            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    }
    )
    public ResponseEntity<List<InsightsBudgetOrSavingsMonthlyTrendDTO>> budgetOrSavingsMonthlyTrend(
            @RequestParam LocalDate start_date,
            @RequestParam LocalDate end_date,
            @RequestParam InsightMonthlyTrendType type)
            throws InternalServerErrorException {
        return new ResponseEntity<>(
                insightsService.getBudgetOrSavingsMonthlyTrend(start_date, end_date, type), HttpStatus.OK
        );
    }

}
