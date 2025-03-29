package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.InsightsBudgetOrSavingsMonthlyTrendDTO;
import com.derrick.finlypal.dto.InsightsMonthlyComparisonDTO;
import com.derrick.finlypal.dto.InsightsSpendByCategoryDTO;
import com.derrick.finlypal.dto.InsightsSpendTrendsDTO;
import com.derrick.finlypal.dto.InsightsTopExpensesDTO;
import com.derrick.finlypal.dto.InsightsTotalBudgetByStatusSummaryDTO;
import com.derrick.finlypal.dto.InsightsTotalSavingsAndBudgetAmountDTO;
import com.derrick.finlypal.dto.InsightsTotalSavingsByStatusSummaryDTO;
import com.derrick.finlypal.dto.InsightsTotalSpendDTO;
import com.derrick.finlypal.enums.BudgetStatus;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.enums.InsightMonthlyTrendType;
import com.derrick.finlypal.enums.SavingsStatus;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.repository.BudgetRepository;
import com.derrick.finlypal.repository.ExpenseRepository;
import com.derrick.finlypal.repository.SavingsRepository;
import com.derrick.finlypal.service.InsightsService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsightsServiceImpl implements InsightsService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsRepository savingsRepository;

    /**
     * Returns the total spend between the given start date and end date for the given expense type.
     * If startDate is null, it defaults to the first day of the current month. If endDate is null, it
     * defaults to today. If type is not {@link ExpenseType#EXPENSE} or {@link ExpenseType#INCOME}, it
     * throws a {@link BadRequestException}.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @param type      the type of expense
     * @return a {@link InsightsTotalSpendDTO} containing the total spend
     * @throws InternalServerErrorException if an unexpected error occurs
     * @throws BadRequestException          if the request is invalid
     */
    @Override
    public InsightsTotalSpendDTO getTotalSpend(
            LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {

        log.info(
                "Received request to get total spend for type {}, between {} and {}",
                type,
                startDate,
                endDate);
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            // Default to the first day of the current month if startDate is null
            if (startDate == null) {
                startDate = LocalDate.now().withDayOfMonth(1);
            }

            // Default to today if endDate is null
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            if (!EnumSet.of(ExpenseType.EXPENSE, ExpenseType.INCOME).contains(type)) {
                throw new BadRequestException("Invalid expense type. Must be EXPENSE or INCOME");
            }

            BigDecimal total = expenseRepository.findTotalAmount(userId, startDate, endDate, type);
            log.info("Total {} is {}", type, total);
            return InsightsTotalSpendDTO.builder().totalSpend(total).build();

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching total spend.");
        }
    }

    /**
     * Retrieves the user's spend by category within the specified date range and for a specific
     * expense type. If the start date or end date is null, it throws a {@link BadRequestException}.
     * If the type is not {@link ExpenseType#EXPENSE} or {@link ExpenseType#INCOME}, it throws a
     * {@link BadRequestException}. It calculates the total spend for each category and computes the
     * percentage share of each category relative to the total spend.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @param type      the type of expense
     * @return a list of {@link InsightsSpendByCategoryDTO} containing spend details by category,
     * including the percentage of total spend for each category
     * @throws InternalServerErrorException if an unexpected error occurs
     * @throws BadRequestException          if the request is invalid
     */
    @Override
    public List<InsightsSpendByCategoryDTO> getSpendByCategory(
            LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get spend by category");
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            if (startDate == null || endDate == null) {
                throw new BadRequestException("Start date and end date cannot be null");
            }

            if (!EnumSet.of(ExpenseType.EXPENSE, ExpenseType.INCOME).contains(type)) {
                throw new BadRequestException("Invalid expense type. Must be EXPENSE or INCOME");
            }

            // Fetch the total amount
            BigDecimal totalAmount = expenseRepository.findTotalAmount(userId, startDate, endDate, type);

            // Ensure totalAmount is not zero to prevent division by zero
            BigDecimal total =
                    (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0)
                            ? totalAmount
                            : BigDecimal.ONE;

            // Fetch spend by category with placeholder percentage
            List<InsightsSpendByCategoryDTO> spendByCategoryList =
                    expenseRepository.findTotalAmountByCategory(userId, startDate, endDate, type);

            // Calculate percentage for each category
            for (InsightsSpendByCategoryDTO spendByCategory : spendByCategoryList) {
                BigDecimal percentage =
                        spendByCategory
                                .getTotalSpend()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(total, 0, RoundingMode.HALF_UP);

                spendByCategory.setPercentage(percentage.intValue());
            }

            return spendByCategoryList;

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching total spend by category.");
        }
    }

    /**
     * Retrieves the user's spend by day within the specified date range and for a specific expense
     * type. If the start date or end date is null, it defaults to today. If the type is not {@link
     * ExpenseType#EXPENSE} or {@link ExpenseType#INCOME}, it throws a {@link BadRequestException}.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @param type      the type of expense
     * @return a list of {@link InsightsSpendTrendsDTO} containing spend details by day
     * @throws InternalServerErrorException if an unexpected error occurs
     * @throws BadRequestException          if the request is invalid
     */
    @Override
    public List<InsightsSpendTrendsDTO> getDailyTrend(
            LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get daily trend");

        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            // Set default dates to today if not provided
            if (startDate == null) {
                startDate = LocalDate.now();
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            if (!EnumSet.of(ExpenseType.EXPENSE, ExpenseType.INCOME).contains(type)) {
                throw new BadRequestException("Invalid expense type. Must be EXPENSE or INCOME");
            }

            // Fetch the data from the repository
            return expenseRepository.findSpendTrends(userId, startDate, endDate, type);

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching daily trend.");
        }
    }

    /**
     * Retrieves the user's spend by month for the given date range and for a specific expense type.
     * If the start date or end date is null, it defaults to the first day and last day of the current
     * year, respectively. If the type is not {@link ExpenseType#EXPENSE} or {@link
     * ExpenseType#INCOME}, it throws a {@link BadRequestException}. It calculates the total spend for
     * each month and computes the percentage share of each month relative to the total spend for the
     * given date range.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @param type      the type of expense
     * @return a list of {@link InsightsMonthlyComparisonDTO} containing spend details by month
     * @throws InternalServerErrorException if an unexpected error occurs
     * @throws BadRequestException          if the request is invalid
     */
    @Override
    public List<InsightsMonthlyComparisonDTO> getMonthlyComparison(
            LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get monthly comparison");
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            // Set default dates to the beginning and end of the current year if not provided
            LocalDate now = LocalDate.now();
            if (startDate == null) {
                startDate = now.with(TemporalAdjusters.firstDayOfYear()); // January 1st
            }
            if (endDate == null) {
                endDate = now.with(TemporalAdjusters.lastDayOfYear()); // December 31st
            }

            if (!EnumSet.of(ExpenseType.EXPENSE, ExpenseType.INCOME).contains(type)) {
                throw new BadRequestException("Invalid expense type. Must be EXPENSE or INCOME");
            }

            // Fetch the data from the repository
            return expenseRepository.findMonthlyExpenseComparison(userId, startDate, endDate, type);

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching monthly comparison.");
        }
    }

    /**
     * Retrieves the top expenses for the given date range and expense type.
     * If the start date or end date is null, it defaults to the first day and last day of the current
     * month, respectively. If the type is not {@link ExpenseType#EXPENSE} or {@link
     * ExpenseType#INCOME}, it throws a {@link BadRequestException}.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @param type      the type of expense
     * @param page      the page number to be returned
     * @param pageSize  the number of items to be returned in each page
     * @return a page of {@link InsightsTopExpensesDTO} containing top expense details
     * @throws InternalServerErrorException if an unexpected error occurs
     * @throws BadRequestException          if the request is invalid
     */
    @Override
    public Page<InsightsTopExpensesDTO> getTopExpenses(
            LocalDate startDate, LocalDate endDate, ExpenseType type, int page, int pageSize)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get top expenses");
        Pageable pageable = PageRequest.of(page, pageSize);
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            // Default to the first day of the current month if startDate is null
            if (startDate == null) {
                startDate = LocalDate.now().withDayOfMonth(1);
            }

            // Default to today if endDate is null
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            if (!EnumSet.of(ExpenseType.EXPENSE, ExpenseType.INCOME).contains(type)) {
                throw new BadRequestException("Invalid expense type. Must be EXPENSE or INCOME");
            }

            return expenseRepository.findTopExpenses(userId, startDate, endDate, type, pageable);

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching top expenses.");
        }
    }

    /**
     * Retrieves the total savings and budget amount for the currently logged in user
     * for the given date range.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @return a {@link InsightsTotalSavingsAndBudgetAmountDTO} containing total savings and
     * budget amount
     * @throws InternalServerErrorException if an unexpected error occurs
     */
    @Override
    public InsightsTotalSavingsAndBudgetAmountDTO getTotalSavingsAndBudgetAmount(
            LocalDate startDate, LocalDate endDate) throws InternalServerErrorException {
        log.info("Received request to get total savings and budget amount");
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            BigDecimal totalSavings = savingsRepository.getTotalTargetAmount(startDate, endDate, userId);
            BigDecimal totalBudget = budgetRepository.getTotalBudget(startDate, endDate, userId);

            return InsightsTotalSavingsAndBudgetAmountDTO
                    .builder()
                    .totalSavings(totalSavings)
                    .totalBudget(totalBudget)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching total savings and budget amount.");
        }
    }

    /**
     * Retrieves the total budget for the currently logged in user for the given date range
     * grouped by the status of the budgets.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @return a list of {@link InsightsTotalBudgetByStatusSummaryDTO} containing total budget
     * by status
     * @throws InternalServerErrorException if an unexpected error occurs
     */
    @Override
    public List<InsightsTotalBudgetByStatusSummaryDTO> getTotalBudgetByStatusSummary(
            LocalDate startDate, LocalDate endDate) throws InternalServerErrorException {
        log.info("Received request to get total budget by status summary");
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            List<Object[]> summary = budgetRepository.getBudgetSummaryByStatus(startDate, endDate, userId);
            return summary.stream()
                    .map(result -> InsightsTotalBudgetByStatusSummaryDTO
                            .builder()
                            .status((BudgetStatus) result[0])
                            .count((Integer) result[1])
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching total budget by status summary.");
        }
    }

    /**
     * Retrieves the total savings amount for the currently logged in user for the given date range
     * grouped by the status of the savings.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @return a list of {@link InsightsTotalSavingsByStatusSummaryDTO} containing total savings
     * by status
     * @throws InternalServerErrorException if an unexpected error occurs
     */
    @Override
    public List<InsightsTotalSavingsByStatusSummaryDTO> getTotalSavingsByStatusSummary(
            LocalDate startDate, LocalDate endDate) throws InternalServerErrorException {
        log.info("Received request to get total savings by status summary");
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            List<Object[]> summary = savingsRepository.getSavingsCountByStatus(startDate, endDate, userId);
            return summary.stream()
                    .map(result -> InsightsTotalSavingsByStatusSummaryDTO
                            .builder()
                            .status((SavingsStatus) result[0])
                            .count((Integer) result[1])
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching total savings by status summary.");
        }
    }

    /**
     * Retrieves the monthly trend of either budget expenses or savings for the currently logged-in user
     * within the specified date range. The trend is determined based on the provided type, which can be
     * either BUDGET or SAVINGS. For BUDGET, it fetches the monthly expense trends, and for SAVINGS, it
     * fetches the saved amount per month. If the type is not valid, it throws a BadRequestException.
     *
     * @param startDate the start date of the range, inclusive
     * @param endDate   the end date of the range, inclusive
     * @param type      the type of trend to retrieve, either BUDGET or SAVINGS
     * @return a list of {@link InsightsBudgetOrSavingsMonthlyTrendDTO} containing the monthly trend data
     * @throws InternalServerErrorException if an unexpected error occurs during the process
     */
    @Override
    public List<InsightsBudgetOrSavingsMonthlyTrendDTO> getBudgetOrSavingsMonthlyTrend(
            LocalDate startDate, LocalDate endDate, InsightMonthlyTrendType type) throws InternalServerErrorException {
        log.info("Received request to get budget or savings monthly trend");
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            List<Object[]> trend = type.equals(InsightMonthlyTrendType.BUDGET)
                    ? budgetRepository.getMonthlyExpenseTrends(startDate, endDate, userId)
                    : savingsRepository.getSavedAmountPerMonth(startDate, endDate, userId);
            return trend.stream()
                    .map(result -> InsightsBudgetOrSavingsMonthlyTrendDTO
                            .builder()
                            .month((String) result[0])
                            .amount((BigDecimal) result[1])
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                    "An unexpected error occurred while fetching budget or savings monthly trend.");
        }
    }
}
