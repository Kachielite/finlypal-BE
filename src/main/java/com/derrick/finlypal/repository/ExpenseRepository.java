package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Expense;
import com.derrick.finlypal.enums.ExpenseType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
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
}
