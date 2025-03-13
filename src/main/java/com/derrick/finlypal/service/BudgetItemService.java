package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetItemCreateRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.BudgetItemUpdateRequestDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BudgetItemService {
    GeneralResponseDTO createBudgetItems(List<BudgetItemCreateRequestDTO> budgetItems, Long budgetId)
            throws BadRequestException,
            InternalServerErrorException,
            NotFoundException,
            NotAuthorizedException;

    Page<BudgetItemResponseDTO> getBudgetItems(Long budgetId, int page, int pageSize)
            throws InternalServerErrorException, NotFoundException, NotAuthorizedException;

    BudgetItemResponseDTO getBudgetItemById(Long budgetItemId)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException;

    BudgetItemResponseDTO updateBudgetItem(
            Long budgetItemId, BudgetItemUpdateRequestDTO budgetItemRequestDTO)
            throws BadRequestException,
            NotFoundException,
            InternalServerErrorException,
            NotAuthorizedException;

    GeneralResponseDTO deleteBudgetItem(Long budgetItemId)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException;
}
