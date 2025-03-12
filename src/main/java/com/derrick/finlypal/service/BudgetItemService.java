package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetItemRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BudgetItemService {
    List<BudgetItemResponseDTO> createBudgetItems(List<BudgetItemRequestDTO> budgetItems, Long budgetId);

    Page<BudgetItemResponseDTO> getBudgetItems(Long budgetId);

    BudgetItemResponseDTO getBudgetItemById(Long budgetItemId);

    BudgetItemResponseDTO updateBudgetItem(Long budgetItemId, BudgetItemRequestDTO budgetItemRequestDTO);

    void deleteBudgetItem(Long budgetItemId);

}
