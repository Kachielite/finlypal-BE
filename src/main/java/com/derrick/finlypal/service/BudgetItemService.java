package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetItemRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BudgetItemService {
    GeneralResponseDTO createBudgetItems(
            List<BudgetItemRequestDTO> budgetItems, Long budgetId)
            throws BadRequestException, InternalServerErrorException, NotFoundException, NotAuthorizedException;

    Page<BudgetItemResponseDTO> getBudgetItems(Long budgetId)
            throws InternalServerErrorException, NotFoundException, NotAuthorizedException;

    BudgetItemResponseDTO getBudgetItemById(Long budgetItemId)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException;

    BudgetItemResponseDTO updateBudgetItem(
            Long budgetItemId, BudgetItemRequestDTO budgetItemRequestDTO)
            throws BadRequestException, NotFoundException, InternalServerErrorException, NotAuthorizedException;

    void deleteBudgetItem(Long budgetItemId) throws NotFoundException, InternalServerErrorException, NotAuthorizedException;
}
