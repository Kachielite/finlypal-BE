package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetRequestDTO;
import com.derrick.finlypal.dto.BudgetResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

public interface BudgetService {
    BudgetResponseDTO createBudget(BudgetRequestDTO budgetRequestDTO) throws BadRequestException, InternalServerErrorException;

    BudgetResponseDTO updateBudget(Long budgetId, BudgetRequestDTO budgetRequestDTO) throws BadRequestException, NotFoundException, NotAuthorizedException, InternalServerErrorException;

    BudgetResponseDTO getBudgetById(Long budgetId) throws NotFoundException, NotAuthorizedException, InternalServerErrorException;

    Page<BudgetResponseDTO> getAllBudgets(int page, int pageSize) throws InternalServerErrorException;

    GeneralResponseDTO deleteBudget(Long budgetId) throws NotFoundException, NotAuthorizedException, InternalServerErrorException;
}
