package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Budget;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Page<Budget> findAllByUserId(Long id, Pageable pageable);

    @Query(
            """
                        SELECT COALESCE(SUM(e.amount), 0)
                        FROM BudgetItem bi
                        LEFT JOIN bi.expenses e
                        ON e.type = 'EXPENSE'
                        WHERE bi.budget.id = :budgetId
                    """)
    BigDecimal findTotalExpensesByBudgetId(@Param("budgetId") Long budgetId);

    @Transactional
    void deleteById(Long id);

    @Query("SELECT COALESCE(SUM(b.totalBudget), 0) " +
            "FROM Budget b " +
            "WHERE b.startDate >= :startDate AND b.endDate <= :endDate " +
            "AND b.user.id = :userId")
    BigDecimal getTotalBudget(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("userId") Long userId);

    @Query("SELECT b.status, COUNT(b) " +
            "FROM Budget b " +
            "WHERE b.startDate >= :startDate AND b.endDate <= :endDate " +
            "AND b.user.id = :userId " +
            "GROUP BY b.status")
    List<Object[]> getBudgetSummaryByStatus(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("userId") Long userId);

    @Query("SELECT FUNCTION('TO_CHAR', e.date, 'Month') AS monthName, SUM(e.amount) " +
            "FROM Expense e " +
            "WHERE e.date >= :startDate AND e.date <= :endDate " +
            "AND e.type = 'EXPENSE' AND e.user.id = :userId " +
            "GROUP BY FUNCTION('TO_CHAR', e.date, 'Month') " +
            "ORDER BY MIN(e.date)")
    List<Object[]> getMonthlyExpenseTrends(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("userId") Long userId);
}
