package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.BudgetItem;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

  Page<BudgetItem> findAllByBudgetId(Long budgetId, Pageable pageable);

  List<BudgetItem> findAllByBudgetId(Long budgetId);
}
