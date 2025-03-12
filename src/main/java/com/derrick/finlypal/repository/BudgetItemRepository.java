package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.BudgetItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

  List<BudgetItem> findAllByBudgetId(Long budgetId);
}
