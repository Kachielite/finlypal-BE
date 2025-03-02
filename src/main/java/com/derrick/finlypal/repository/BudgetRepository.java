package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Month;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Page<Budget> findAllByUserId(Long userId, Pageable pageable);

    Page<Budget> findAllByUserIdAndMonth(Long userId, Month month, Pageable pageable);


}
