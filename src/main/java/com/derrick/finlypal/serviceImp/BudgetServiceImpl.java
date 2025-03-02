package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.BudgetResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetService budgetService;

    @Override
    public String createBudget(List<BudgetResponseDTO> budgets) throws InternalServerErrorException {
        return "";
    }

    @Override
    public String updateBudget(List<BudgetResponseDTO> budgets) throws InternalServerErrorException {
        return "";
    }

    @Override
    public String deleteBudgets(List<Integer> budgetIds) throws NotFoundException, InternalServerErrorException {
        return "";
    }

    @Override
    public Page<BudgetResponseDTO[]> getBudgetByUserId(int page, int pageSize) throws InternalServerErrorException {
        return null;
    }

    @Override
    public Page<BudgetResponseDTO[]> getBudgetByUserIdAndMonth(String month, int page, int pageSize) throws InternalServerErrorException {
        return null;
    }
}
