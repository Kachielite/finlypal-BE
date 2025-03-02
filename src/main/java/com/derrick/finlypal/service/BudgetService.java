package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.BudgetResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BudgetService {

    String createBudget(List<BudgetResponseDTO> budgets) throws InternalServerErrorException;

    String updateBudget(List<BudgetResponseDTO> budgets) throws InternalServerErrorException;

    String deleteBudgets(List<Integer> budgetIds) throws NotFoundException, InternalServerErrorException;

    Page<BudgetResponseDTO[]> getBudgetByUserId(int page, int pageSize) throws InternalServerErrorException;

    Page<BudgetResponseDTO[]> getBudgetByUserIdAndMonth(String month, int page, int pageSize) throws InternalServerErrorException;
}
