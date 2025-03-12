package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetRequestDTO;
import com.derrick.finlypal.dto.BudgetResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

public interface BudgetService {
    BudgetResponseDTO createBudget(BudgetRequestDTO budgetRequestDTO) throws BadRequestException, InternalServerErrorException;

    BudgetResponseDTO updateBudget(Long budgetId, BudgetRequestDTO budgetRequestDTO) throws BadRequestException, NotFoundException, InternalServerErrorException;

    BudgetResponseDTO getBudgetById(Long budgetId) throws NotFoundException, InternalServerErrorException;

    Page<BudgetResponseDTO> getAllBudgets(int page, int pageSize) throws InternalServerErrorException;

    void deleteBudget(Long budgetId) throws NotFoundException, InternalServerErrorException;
}
