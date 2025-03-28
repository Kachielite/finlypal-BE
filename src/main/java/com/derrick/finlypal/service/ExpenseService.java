package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.ExpenseRequestDTO;
import com.derrick.finlypal.dto.ExpenseResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

public interface ExpenseService {
  ExpenseResponseDTO findById(Long id)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException;

  Page<ExpenseResponseDTO> findAllByUserIdOrDateBetweenOrTypeOrCategoryId(
      ExpenseType expenseType,
      LocalDate startDate,
      LocalDate endDate,
      Long categoryId,
      int page,
      int pageSize)
      throws InternalServerErrorException, BadRequestException;

  ExpenseResponseDTO addExpense(ExpenseRequestDTO expenseRequestDTO)
      throws InternalServerErrorException, BadRequestException;

  ExpenseResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO expenseRequestDTO)
      throws NotFoundException, InternalServerErrorException, NotAuthorizedException;

  GeneralResponseDTO deleteExpense(Long id)
      throws InternalServerErrorException, NotAuthorizedException, NotFoundException;
}
