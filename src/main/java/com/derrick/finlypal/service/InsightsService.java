package com.derrick.finlypal.service;

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
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface InsightsService {
    InsightsTotalSpendDTO getTotalSpend(LocalDate startDate, LocalDate endData, ExpenseType type)
            throws InternalServerErrorException, BadRequestException;

    List<InsightsSpendByCategoryDTO> getSpendByCategory(
            LocalDate startDate, LocalDate endData, ExpenseType type)
            throws InternalServerErrorException, BadRequestException;

    List<InsightsSpendTrendsDTO> getDailyTrend(
            LocalDate startDate, LocalDate endData, ExpenseType type)
            throws InternalServerErrorException, BadRequestException;

    List<InsightsMonthlyComparisonDTO> getMonthlyComparison(
            LocalDate startDate, LocalDate endData, ExpenseType type)
            throws InternalServerErrorException, BadRequestException;

    Page<InsightsTopExpensesDTO> getTopExpenses(
            LocalDate startDate, LocalDate endData, ExpenseType type, int page, int pageSize)
            throws InternalServerErrorException, BadRequestException;

    InsightsTotalSavingsAndBudgetAmountDTO getTotalSavingsAndBudgetAmount(
            LocalDate startDate, LocalDate endDate
    ) throws InternalServerErrorException;

    List<InsightsTotalBudgetByStatusSummaryDTO> getTotalBudgetByStatusSummary(
            LocalDate startDate, LocalDate endDate
    ) throws InternalServerErrorException;

    List<InsightsTotalSavingsByStatusSummaryDTO> getTotalSavingsByStatusSummary(
            LocalDate startDate, LocalDate endDate
    ) throws InternalServerErrorException;

    List<InsightsBudgetOrSavingsMonthlyTrendDTO> getBudgetOrSavingsMonthlyTrend(
            LocalDate startDate, LocalDate endDate, InsightMonthlyTrendType type
    ) throws InternalServerErrorException;
}
