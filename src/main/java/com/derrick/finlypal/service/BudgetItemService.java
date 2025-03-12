package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetItemRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;

public interface BudgetItemService {
  List<BudgetItemResponseDTO> createBudgetItems(
      List<BudgetItemRequestDTO> budgetItems, Long budgetId)
      throws BadRequestException, InternalServerErrorException;

  Page<BudgetItemResponseDTO> getBudgetItems(Long budgetId)
      throws InternalServerErrorException, NotFoundException;

  BudgetItemResponseDTO getBudgetItemById(Long budgetItemId)
      throws NotFoundException, InternalServerErrorException;

  BudgetItemResponseDTO updateBudgetItem(
      Long budgetItemId, BudgetItemRequestDTO budgetItemRequestDTO)
      throws BadRequestException, NotFoundException, InternalServerErrorException;

  void deleteBudgetItem(Long budgetItemId) throws NotFoundException, InternalServerErrorException;
}
