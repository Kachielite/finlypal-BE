package com.derrick.finlypal.repository;

import com.derrick.finlypal.dto.InsightsResponseDTO;
import com.derrick.finlypal.entity.Expense;
import com.derrick.finlypal.enums.ExpenseType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findById(Long id);

    Page<Expense> findAllByUserId(Long userId, Pageable pageable);

    Page<Expense> findAllByCategoryIdAndUserId(Long categoryId, Long userId, Pageable pageable);

    Page<Expense> findAllByUserIdAndDateBetween(
            Long userId,
            @NotNull(message = "Start date is required")
            LocalDate startDate,
            @NotNull(message = "End date is required")
            @FutureOrPresent(message = "End date must not be less than start date")
            LocalDate endDate,
            Pageable pageable
    );

    Page<Expense> findAllByTypeAndUserIdOrDateBetween(
            ExpenseType type,
            Long userId,
            LocalDate startDate,
            @FutureOrPresent(message = "End date must not be less than start date")
            LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "AND (:type IS NULL OR e.type = :type)")
    InsightsResponseDTO.TotalSpend findTotalExpenses(@Param("userId") Long userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("type") ExpenseType type);

    @Query("SELECT new com.derrick.finlypal.dto.InsightsResponseDTO.SpendByCategory(" +
            "c.name, " +
            "SUM(e.amount), " +
            "CAST((SUM(e.amount) * 100.0 / :totalAmount) AS integer)) " +
            "FROM Expense e " +
            "JOIN e.category c " +
            "WHERE e.user.id = :userId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "AND (:type IS NULL OR e.type = :type) " +
            "GROUP BY c.name")
    List<InsightsResponseDTO.SpendByCategory> findTotalAmountByCategory(@Param("userId") Long userId,
                                                                        @Param("startDate") LocalDate startDate,
                                                                        @Param("endDate") LocalDate endDate,
                                                                        @Param("type") ExpenseType type,
                                                                        @Param("totalAmount") BigDecimal totalAmount);


    @Query("SELECT new com.derrick.finlypal.dto.InsightsResponseDTO.TopExpenses(e.description, e.amount, e.date) " +
            "FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "AND (:type IS NULL OR e.type = :type) " +
            "ORDER BY e.amount DESC")
    List<InsightsResponseDTO.TopExpenses> findTopExpenses(@Param("userId") Long userId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate,
                                                          @Param("type") ExpenseType type,
                                                          Pageable pageable);


    @Query("SELECT new com.derrick.finlypal.dto.InsightsResponseDTO.MonthlyComparison( " +
            "FUNCTION('DATE_TRUNC', 'month', e.date), e.type, COALESCE(SUM(e.amount), 0)) " +
            "FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_TRUNC', 'month', e.date), e.type " +
            "ORDER BY FUNCTION('DATE_TRUNC', 'month', e.date), e.type")
    List<InsightsResponseDTO.MonthlyComparison> findMonthlyExpenseComparison(@Param("userId") Long userId,
                                                                             @Param("startDate") LocalDate startDate,
                                                                             @Param("endDate") LocalDate endDate,
                                                                             @Param("type") ExpenseType type);

    @Query("SELECT new com.derrick.finlypal.dto.InsightsResponseDTO.SpendTrend(e.date, SUM(e.amount)) " +
            "FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "AND (:type IS NULL OR e.type = :type) " +
            "GROUP BY e.date " +
            "ORDER BY e.date")
    List<InsightsResponseDTO.SpendTrend> findSpendTrends(@Param("userId") Long userId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate,
                                                         @Param("type") ExpenseType type);

}
