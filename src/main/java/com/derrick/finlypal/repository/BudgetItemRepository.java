package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

    List<BudgetItem> findAllByBudgetId(Long budgetId);
}
