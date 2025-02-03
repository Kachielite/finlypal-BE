package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.InsightsResponseDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;

import java.time.LocalDate;
import java.util.List;

public interface InsightsService {
    InsightsResponseDTO.TotalSpend getTotalSpend(
            LocalDate startDate,
            LocalDate endData,
            ExpenseType type
    ) throws InternalServerErrorException, BadRequestException;

    List<InsightsResponseDTO.SpendByCategory> getSpendByCategory(
            LocalDate startDate,
            LocalDate endData,
            ExpenseType type
    ) throws InternalServerErrorException, BadRequestException;

    List<InsightsResponseDTO.SpendTrend> getDailyTrend(
            LocalDate startDate,
            LocalDate endData,
            ExpenseType type
    ) throws InternalServerErrorException, BadRequestException;

    List<InsightsResponseDTO.MonthlyComparison> getMonthlyComparison(
            LocalDate startDate,
            LocalDate endData,
            ExpenseType type
    ) throws InternalServerErrorException, BadRequestException;

    List<InsightsResponseDTO.TopExpenses> getTopExpenses(
            LocalDate startDate,
            LocalDate endData,
            ExpenseType type,
            int page,
            int pageSize
    ) throws InternalServerErrorException, BadRequestException;
}

