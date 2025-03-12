package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetRequestDTO;
import com.derrick.finlypal.dto.BudgetResponseDTO;
import org.springframework.data.domain.Page;

public interface BudgetService {
    BudgetResponseDTO createBudget(BudgetRequestDTO budgetRequestDTO);

    BudgetResponseDTO updateBudget(Long budgetId, BudgetRequestDTO budgetRequestDTO);

    BudgetResponseDTO getBudgetById(Long budgetId);

    Page<BudgetResponseDTO> getAllBudgets(int page, int pageSize);

    void deleteBudget(Long budgetId);
}
