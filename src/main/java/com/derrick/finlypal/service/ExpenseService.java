package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.ExpenseRequestDTO;
import com.derrick.finlypal.dto.ExpenseResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ExpenseService {
    ExpenseResponseDTO findById(Long id)
            throws NotFoundException, NotAuthorizedException, InternalServerErrorException;

    Page<ExpenseResponseDTO> findAllByUserId(int page, int pageSize)
            throws InternalServerErrorException;

    Page<ExpenseResponseDTO> findAllByCategoryIdAndUserId(Long categoryId, int page, int pageSize)
            throws InternalServerErrorException;

    Page<ExpenseResponseDTO> findAllByDateBetween(
            LocalDate startDate, LocalDate endDate, int page, int pageSize)
            throws BadRequestException, InternalServerErrorException;

    Page<ExpenseResponseDTO> findAllByTypeAndUserIdOrDateBetween(
            ExpenseType expenseType, LocalDate startDate, LocalDate endDate, int page, int pageSize)
            throws BadRequestException, InternalServerErrorException;

    Page<ExpenseResponseDTO> findAllByUserIdAndDateBetweenOrTypeOrCategoryId(
            ExpenseType expenseType, LocalDate startDate, LocalDate endDate, Long categoryId, int page, int pageSize)
            throws InternalServerErrorException;

    GeneralResponseDTO addExpense(ExpenseRequestDTO expenseRequestDTO)
            throws InternalServerErrorException, BadRequestException;

    GeneralResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO expenseRequestDTO)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException;

    GeneralResponseDTO deleteExpense(Long id)
            throws InternalServerErrorException, NotAuthorizedException, NotFoundException;
}
