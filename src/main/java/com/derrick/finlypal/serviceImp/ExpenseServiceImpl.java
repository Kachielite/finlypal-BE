package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.ExpenseRequestDTO;
import com.derrick.finlypal.dto.ExpenseResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.entity.Category;
import com.derrick.finlypal.entity.Expense;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.enums.ExpenseType;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.CategoryRepository;
import com.derrick.finlypal.repository.ExpenseRepository;
import com.derrick.finlypal.service.ExpenseService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ExpenseResponseDTO findById(Long id)
            throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
        log.info("Finding expense with id {}", id);
        try {
            Expense expense = expenseRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Could not find expense with id")
            );

            Long expenseUserId = expense.getUser().getId();
            Long loggedInUserId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            if (expenseUserId.equals(loggedInUserId)) {
                throw new NotAuthorizedException("You are not authorized to view this expense");
            }

            log.info("Found expense with id {}", id);
            return ExpenseResponseDTO
                    .builder()
                    .id(expense.getId())
                    .date(expense.getDate())
                    .amount(expense.getAmount())
                    .description(expense.getDescription())
                    .category_id(expense.getCategory().getId())
                    .build();

        } catch (NotFoundException e) {
            log.info("Could not find expense with id {}", id);
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("User with id {} is not authorized to view this expense", id);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while trying to find expense with id {}", id);
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public Page<ExpenseResponseDTO> findAllByUserId(int page, int pageSize)
            throws InternalServerErrorException {
        Pageable pageable = PageRequest.of(page, pageSize);
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            log.info("Finding expenses with user id {}", userId);
            Page<Expense> expenses = expenseRepository.findAllByUserId(userId, pageable);

            log.info("Found {} expenses", expenses.getTotalElements());
            return convertExpenseToExpenseDTO(expenses);
        } catch (Exception e) {
            log.error("Unexpected error occurred while trying to find expenses for user");
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public Page<ExpenseResponseDTO> findAllByCategoryIdAndUserId(Long categoryId, int page, int pageSize)
            throws InternalServerErrorException {
        log.info("Finding expenses with category id {}", categoryId);
        Pageable pageable = PageRequest.of(page, pageSize);
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            Page<Expense> expenses = expenseRepository.findAllByCategoryIdAndUserId(categoryId, userId, pageable);

            log.info("Found {} expenses for category id {}", expenses.getTotalElements(), categoryId);
            return convertExpenseToExpenseDTO(expenses);
        } catch (Exception e) {
            log.error("Unexpected error occurred while trying to find expenses for category id {}", categoryId);
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @Override
    public Page<ExpenseResponseDTO> findAllByDateBetween(LocalDate startDate, LocalDate endDate, int page, int pageSize)
            throws BadRequestException, InternalServerErrorException {
        log.info("Finding expenses with date between {} and {}", startDate, endDate);

        Pageable pageable = PageRequest.of(page, pageSize);
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            if (startDate.isAfter(endDate)) {
                throw new BadRequestException("Start date cannot be after end date");
            }

            Page<Expense> expenses = expenseRepository.findAllByUserIdAndDateBetween(userId, startDate, endDate, pageable);

            log.info("Found {} expenses for dates between {} and {}", expenses.getTotalElements(), startDate, endDate);
            return convertExpenseToExpenseDTO(expenses);
        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public Page<ExpenseResponseDTO> findAllByTypeAndUserIdOrDateBetween(ExpenseType expenseType, LocalDate startDate, LocalDate endDate, int page, int pageSize)
            throws BadRequestException, InternalServerErrorException {
        log.info("Finding expenses of type {} with date between {} and {}", expenseType, startDate, endDate);
        Pageable pageable = PageRequest.of(page, pageSize);

        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            if (startDate != null && endDate == null) {
                throw new BadRequestException("Start date is required when end date is provided");
            }

            if (startDate == null && endDate != null) {
                throw new BadRequestException("End date is required when start date is null");
            }

            if (startDate != null && startDate.isAfter(endDate)) {
                throw new BadRequestException("Start date cannot be after end date");
            }

            log.info("Searching expenses of type {} with date between {} and {}", expenseType, startDate, endDate);
            Page<Expense> expenses = expenseRepository.findAllByTypeAndUserIdOrDateBetween(expenseType, userId, startDate, endDate, pageable);

            log.info("Successfully found {} expenses of type {}", expenses.getTotalElements(), expenseType);
            return convertExpenseToExpenseDTO(expenses);

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @Override
    public GeneralResponseDTO addExpense(ExpenseRequestDTO expenseRequestDTO)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received new expense request for {}", expenseRequestDTO);
        try {
            User user = Objects.requireNonNull(GetLoggedInUserUtil.getUser());
            Category category = categoryRepository.findById(expenseRequestDTO.categoryID())
                    .orElseThrow(
                            () -> new BadRequestException("Could not find category with id: " + expenseRequestDTO.categoryID())
                    );

            log.info("Found category with id {}", category.getId());
            Expense expense = Expense
                    .builder()
                    .description(expenseRequestDTO.description())
                    .amount(expenseRequestDTO.amount())
                    .user(user)
                    .date(expenseRequestDTO.date())
                    .category(category)
                    .build();

            log.info("Saving expense {}", expense);
            expenseRepository.save(expense);

            log.info("Saved expense {}", expense);
            return GeneralResponseDTO
                    .builder()
                    .status(HttpStatus.CREATED)
                    .message("Successfully created expense")
                    .build();

        } catch (BadRequestException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public GeneralResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO expenseRequestDTO)
            throws InternalServerErrorException, NotFoundException, NotAuthorizedException {
        log.info("Received update expense request for {}", expenseRequestDTO);

        try {
            User user = Objects.requireNonNull(GetLoggedInUserUtil.getUser());
            Expense expense = expenseRepository.findById(expenseId).orElseThrow(
                    () -> new NotFoundException("Could not find expense with id: " + expenseId)
            );

            if (!Objects.equals(expense.getUser().getId(), user.getId())) {
                throw new NotAuthorizedException("You do not have permission to update this expense");
            }

            if (expenseRequestDTO.description() != null) {
                expense.setDescription(expenseRequestDTO.description());
            }

            if (expenseRequestDTO.amount() != null) {
                expense.setAmount(expenseRequestDTO.amount());
            }

            if (expenseRequestDTO.date() != null) {
                expense.setDate(expenseRequestDTO.date());
            }

            if (expenseRequestDTO.categoryID() != null) {
                Category category = categoryRepository.findById(expenseRequestDTO.categoryID()).orElseThrow(
                        () -> new NotFoundException("Could not find category with id: " + expenseRequestDTO.categoryID())
                );

                expense.setCategory(category);
            }

            log.info("Updating expense {}", expense);
            expenseRepository.save(expense);

            log.info("Successfully updated expense {}", expense);
            return GeneralResponseDTO
                    .builder()
                    .status(HttpStatus.OK)
                    .message("Successfully updated expense")
                    .build();


        } catch (NotFoundException | NotAuthorizedException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public GeneralResponseDTO deleteExpense(Long id)
            throws InternalServerErrorException, NotAuthorizedException, NotFoundException {
        log.info("Received delete expense request for {}", id);
        try {
            User user = Objects.requireNonNull(GetLoggedInUserUtil.getUser());
            Expense expense = expenseRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Could not find expense with id:" + id)
            );

            if (!Objects.equals(expense.getUser().getId(), user.getId())) {
                throw new NotAuthorizedException("You do not have permission to delete this expense");
            }

            log.info("Deleting expense {}", expense);
            expenseRepository.delete(expense);

            log.info("Deleted expense {}", expense);
            return GeneralResponseDTO
                    .builder()
                    .status(HttpStatus.OK)
                    .message("Successfully deleted expense")
                    .build();

        } catch (NotFoundException | NotAuthorizedException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private Page<ExpenseResponseDTO> convertExpenseToExpenseDTO(Page<Expense> expenses) {
        return expenses.map(expense -> ExpenseResponseDTO
                .builder()
                .id(expense.getId())
                .date(expense.getDate())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .category_id(expense.getCategory().getId())
                .build()
        );
    }

}
