package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.InsightsResponseDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.repository.ExpenseRepository;
import com.derrick.finlypal.service.InsightsService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class InsightsServiceImpl implements InsightsService {

    Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
    private ExpenseRepository expenseRepository;

    @Override
    public InsightsResponseDTO.TotalSpend getTotalSpend(LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {

        log.info("Received request to get total spend");
        try {

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

            return expenseRepository.findTotalExpenses(userId, startDate, endDate, type);

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An unexpected error occurred while fetching total spend.");
        }
    }

    @Override
    public List<InsightsResponseDTO.SpendByCategory> getSpendByCategory(LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get spend by category");
        try {
            if (startDate == null || endDate == null) {
                throw new BadRequestException("Start date and end date cannot be null");
            }

            if (!EnumSet.of(ExpenseType.EXPENSE, ExpenseType.INCOME).contains(type)) {
                throw new BadRequestException("Invalid expense type. Must be EXPENSE or INCOME");
            }

            BigDecimal totalAmount = expenseRepository.findTotalExpenses(userId, startDate, endDate, type).getTotalSpend();

            return expenseRepository.findTotalAmountByCategory(userId, startDate, endDate, type, totalAmount);
        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new InternalServerErrorException("An unexpected error occurred while fetching total spend by category.");
        }
    }

    @Override
    public List<InsightsResponseDTO.SpendTrend> getDailyTrend(LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get daily trend");

        try {
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
            throw new InternalServerErrorException("An unexpected error occurred while fetching daily trend.");
        }
    }

    @Override
    public List<InsightsResponseDTO.MonthlyComparison> getMonthlyComparison(LocalDate startDate, LocalDate endDate, ExpenseType type)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get monthly comparison");
        try {
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
            throw new InternalServerErrorException("An unexpected error occurred while fetching monthly comparison.");
        }

    }

    @Override
    public List<InsightsResponseDTO.TopExpenses> getTopExpenses(LocalDate startDate, LocalDate endDate, ExpenseType type, int page, int pageSize)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get top expenses");
        Pageable pageable = PageRequest.of(page, pageSize);
        try {

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
            throw new InternalServerErrorException("An unexpected error occurred while fetching top expenses.");
        }
    }
}
