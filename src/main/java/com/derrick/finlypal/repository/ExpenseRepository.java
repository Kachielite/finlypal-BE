package com.derrick.finlypal.repository;

import com.derrick.finlypal.dto.InsightsMonthlyComparisonDTO;
import com.derrick.finlypal.dto.InsightsSpendByCategoryDTO;
import com.derrick.finlypal.dto.InsightsSpendTrendsDTO;
import com.derrick.finlypal.dto.InsightsTopExpensesDTO;
import com.derrick.finlypal.dto.InsightsTotalSpendDTO;
import com.derrick.finlypal.entity.Expense;
import com.derrick.finlypal.enums.ExpenseType;
import jakarta.validation.constraints.FutureOrPresent;
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

    Page<Expense> findAllByUserIdAndDateBetweenOrTypeOrCategoryId(
            Long userId,
            LocalDate startDate,
            @FutureOrPresent(message = "End date must not be less than start date")
            LocalDate endDate,
            ExpenseType type,
            Long categoryId,
            Pageable pageable
    );

    @Query(
            "SELECT COALESCE(SUM(e.amount), 0) FROM Expense e "
                    + "WHERE e.user.id = :userId "
                    + "AND e.date BETWEEN :startDate AND :endDate "
                    + "AND (:type IS NULL OR e.type = :type)")
    InsightsTotalSpendDTO findTotalExpenses(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") ExpenseType type);

    @Query(
            "SELECT CAST(COALESCE(SUM(e.amount), 0) AS bigdecimal) FROM Expense e "
                    + "WHERE e.user.id = :userId "
                    + "AND e.date BETWEEN :startDate AND :endDate "
                    + "AND (:type IS NULL OR e.type = :type)")
    BigDecimal findTotalAmount(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") ExpenseType type);

    @Query(
            "SELECT new com.derrick.finlypal.dto.InsightsSpendByCategoryDTO("
                    + "c.name, "
                    + "SUM(e.amount), "
                    + "0) "
                    + // Set a placeholder (e.g., 0) for percentage
                    "FROM Expense e "
                    + "JOIN e.category c "
                    + "WHERE e.user.id = :userId "
                    + "AND e.date BETWEEN :startDate AND :endDate "
                    + "AND (:type IS NULL OR e.type = :type) "
                    + "GROUP BY c.name")
    List<InsightsSpendByCategoryDTO> findTotalAmountByCategory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") ExpenseType type);

    @Query(
            value =
                    "SELECT new com.derrick.finlypal.dto.InsightsTopExpensesDTO(e.description, e.amount, e.date) "
                            + "FROM Expense e "
                            + "WHERE e.user.id = :userId "
                            + "AND e.date BETWEEN :startDate AND :endDate "
                            + "AND (:type IS NULL OR e.type = :type) "
                            + "ORDER BY e.amount DESC",
            countQuery =
                    "SELECT COUNT(e) "
                            + "FROM Expense e "
                            + "WHERE e.user.id = :userId "
                            + "AND e.date BETWEEN :startDate AND :endDate "
                            + "AND (:type IS NULL OR e.type = :type)")
    Page<InsightsTopExpensesDTO> findTopExpenses(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") ExpenseType type,
            Pageable pageable);

    @Query(
            "SELECT new com.derrick.finlypal.dto.InsightsMonthlyComparisonDTO( "
                    + "TO_CHAR(e.date, 'YYYY-MM'), "
                    + // Replaces DATE_FORMAT
                    "COALESCE(SUM(e.amount), 0E0), "
                    + "e.type) "
                    + "FROM Expense e "
                    + "WHERE e.user.id = :userId "
                    + "AND e.date BETWEEN :startDate AND :endDate "
                    + "AND (:type IS NULL OR e.type = :type) "
                    + "GROUP BY TO_CHAR(e.date, 'YYYY-MM'), e.type "
                    + "ORDER BY TO_CHAR(e.date, 'YYYY-MM'), e.type")
    List<InsightsMonthlyComparisonDTO> findMonthlyExpenseComparison(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") ExpenseType type);

    @Query(
            "SELECT new com.derrick.finlypal.dto.InsightsSpendTrendsDTO(e.date, SUM(e.amount)) "
                    + "FROM Expense e "
                    + "WHERE e.user.id = :userId "
                    + "AND e.date BETWEEN :startDate AND :endDate "
                    + "AND (:type IS NULL OR e.type = :type) "
                    + "GROUP BY e.date "
                    + "ORDER BY e.date")
    List<InsightsSpendTrendsDTO> findSpendTrends(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") ExpenseType type);
}
