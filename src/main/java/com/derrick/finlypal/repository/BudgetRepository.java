package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Page<Budget> findAllByUserId(Long id, Pageable pageable);

}
