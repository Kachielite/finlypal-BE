package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Savings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SavingsRepository extends JpaRepository<Savings, Long> {
    Page<Savings> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.targetAmount), 0) " +
            "FROM Savings s " +
            "WHERE s.startDate >= :startDate AND s.endDate <= :endDate " +
            "AND s.user.id = :userId")
    BigDecimal getTotalTargetAmount(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("userId") Long userId);

    @Query("SELECT s.status, COUNT(s) " +
            "FROM Savings s " +
            "WHERE s.startDate >= :startDate AND s.endDate <= :endDate " +
            "AND s.user.id = :userId " +
            "GROUP BY s.status")
    List<Object[]> getSavingsCountByStatus(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("userId") Long userId);

    @Query("SELECT FUNCTION('TO_CHAR', s.startDate, 'Month') AS monthName, SUM(s.savedAmount) " +
            "FROM Savings s " +
            "WHERE s.startDate >= :startDate AND s.endDate <= :endDate " +
            "AND s.user.id = :userId " +
            "GROUP BY FUNCTION('TO_CHAR', s.startDate, 'Month') " +
            "ORDER BY MIN(s.startDate)")
    List<Object[]> getSavedAmountPerMonth(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("userId") Long userId);
}
