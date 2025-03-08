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
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final CategoryRepository categoryRepository;

  /**
   * This method is used to find an expense by its id. It returns an {@link ExpenseResponseDTO} if
   * the expense is found, otherwise it throws a {@link NotFoundException}. It also checks if the
   * user is authorized to view the expense by comparing the user id of the expense and the logged
   * in user. If the user is not authorized, it throws a {@link NotAuthorizedException}. If any
   * unexpected error occurs while trying to find the expense, it throws an {@link
   * InternalServerErrorException}.
   *
   * @param expense_id the id of the expense to be found
   * @return the expense found, represented as an {@link ExpenseResponseDTO}
   * @throws NotFoundException if the expense with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to view the expense
   * @throws InternalServerErrorException if any unexpected error occurs while trying to find the
   *     expense
   */
  @Override
  public ExpenseResponseDTO findById(Long expense_id)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
    log.info("Finding expense with id {}", expense_id);
    try {
      Expense expense =
          expenseRepository
              .findById(expense_id)
              .orElseThrow(() -> new NotFoundException("Could not find expense with id"));

      Long expenseUserId = expense.getUser().getId();
      Long loggedInUserId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      log.info("expenseUserId {}", expenseUserId);
      log.info("loggedInUserId {}", loggedInUserId);

      if (!expenseUserId.equals(loggedInUserId)) {
        throw new NotAuthorizedException("You are not authorized to view this expense");
      }

      log.info("Found expense with id {}", expense_id);
      return ExpenseResponseDTO.builder()
          .id(expense.getId())
          .date(expense.getDate())
          .amount(expense.getAmount())
          .description(expense.getDescription())
          .categoryId(expense.getCategory().getId())
          .categoryName(expense.getCategory().getDisplayName())
          .build();

    } catch (NotFoundException e) {
      log.info("Could not find expense with id {}", expense_id);
      throw e;
    } catch (NotAuthorizedException e) {
      log.error("User with id {} is not authorized to view this expense", expense_id);
      throw e;
    } catch (Exception e) {
      log.error("Unexpected error occurred while trying to find expense with id {}", expense_id);
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  /**
   * This method is used to find all expenses associated with the currently logged in user based on
   * the given criteria. It can be used to find expenses by type, date range and category id. If the
   * start date is provided but the end date is not, it throws a {@link BadRequestException}. If the
   * end date is provided but the start date is not, it throws a {@link BadRequestException}. If the
   * start date is after the end date, it throws a {@link BadRequestException}. If any unexpected
   * error occurs while trying to find the expenses, it throws an {@link
   * InternalServerErrorException}.
   *
   * @param expenseType the type of expense to be found
   * @param startDate the start date of the range, inclusive
   * @param endDate the end date of the range, inclusive
   * @param categoryId the category id of the expense, if provided
   * @param page the page number to be returned
   * @param pageSize the number of items to be returned in each page
   * @return a page of expenses found, represented as an {@link ExpenseResponseDTO}
   * @throws BadRequestException if the request is invalid
   * @throws InternalServerErrorException if any unexpected error occurs while trying to find the
   *     expenses
   */
  @Override
  public Page<ExpenseResponseDTO> findAllByUserIdOrDateBetweenOrTypeOrCategoryId(
      ExpenseType expenseType,
      LocalDate startDate,
      LocalDate endDate,
      Long categoryId,
      int page,
      int pageSize)
      throws InternalServerErrorException, BadRequestException {

    log.info(
        "Filtering expenses: Type: {}, Dates: {} - {}, Category: {}",
        expenseType,
        startDate,
        endDate,
        categoryId);

    if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
      throw new BadRequestException("Both start date and end date must be provided together.");
    }

    if (startDate != null && startDate.isAfter(endDate)) {
      throw new BadRequestException("Start date cannot be after end date.");
    }

    if (startDate != null && endDate.isBefore(startDate)) {
      throw new BadRequestException("End date must not be before start date");
    }

    Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
    Pageable pageable = PageRequest.of(page, pageSize);

    try {
      Page<Expense> expenses =
          expenseRepository.findAllByFilters(
              userId, startDate, endDate, expenseType, categoryId, pageable);
      log.info("Found {} expenses", expenses.getTotalElements());
      return convertExpenseToExpenseDTO(expenses);
    } catch (Exception e) {
      log.error("Error fetching expenses: {}", e.getMessage());
      throw new InternalServerErrorException("Error retrieving expenses.");
    }
  }

  /**
   * Adds a new expense to the database. It takes in an {@link ExpenseRequestDTO} as a parameter,
   * and returns a {@link GeneralResponseDTO} indicating the status of the request. If the request
   * is successful, it returns a status of {@link HttpStatus#CREATED} and a message indicating that
   * the expense was successfully created. If the request is invalid, it throws a {@link
   * BadRequestException}. If any unexpected error occurs while trying to add the expense, it throws
   * an {@link InternalServerErrorException}.
   *
   * @param expenseRequestDTO the expense request
   * @return a {@link GeneralResponseDTO} indicating the status of the request
   * @throws BadRequestException if the request is invalid
   * @throws InternalServerErrorException if any unexpected error occurs while trying to add the
   *     expense
   */
  @Override
  public GeneralResponseDTO addExpense(ExpenseRequestDTO expenseRequestDTO)
      throws InternalServerErrorException, BadRequestException {
    log.info("Received new expense request for {}", expenseRequestDTO);
    try {
      User user = Objects.requireNonNull(GetLoggedInUserUtil.getUser());
      Category category =
          categoryRepository
              .findById(expenseRequestDTO.categoryID())
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "Could not find category with id: " + expenseRequestDTO.categoryID()));

      log.info("Found category with id {}", category.getId());
      Expense expense =
          Expense.builder()
              .description(expenseRequestDTO.description())
              .type(expenseRequestDTO.type())
              .amount(expenseRequestDTO.amount())
              .user(user)
              .date(expenseRequestDTO.date())
              .category(category)
              .build();

      log.info("Saving expense {}", expense);
      expenseRepository.save(expense);

      log.info("Saved expense {}", expense);
      return GeneralResponseDTO.builder()
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

  /**
   * Updates an existing expense with the given {@link ExpenseRequestDTO}. It takes in the id of the
   * expense to be updated and the {@link ExpenseRequestDTO} containing the new values. It returns a
   * {@link GeneralResponseDTO} indicating the status of the request. If the request is successful,
   * it returns a status of {@link HttpStatus#OK} and a message indicating that the expense was
   * successfully updated. If the expense with the given id is not found, it throws a {@link
   * NotFoundException}. If the user is not authorized to update the expense, it throws a {@link
   * NotAuthorizedException}. If any unexpected error occurs while trying to update the expense, it
   * throws an {@link InternalServerErrorException}.
   *
   * @param expenseId the id of the expense to be updated
   * @param expenseRequestDTO the expense request containing the new values
   * @return a {@link GeneralResponseDTO} indicating the status of the request
   * @throws NotFoundException if the expense with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to update the expense
   * @throws InternalServerErrorException if any unexpected error occurs while trying to update the
   *     expense
   */
  @Override
  public GeneralResponseDTO updateExpense(Long expenseId, ExpenseRequestDTO expenseRequestDTO)
      throws InternalServerErrorException, NotFoundException, NotAuthorizedException {
    log.info("Received update expense request for {}", expenseRequestDTO);

    try {
      User user = Objects.requireNonNull(GetLoggedInUserUtil.getUser());
      Expense expense =
          expenseRepository
              .findById(expenseId)
              .orElseThrow(
                  () -> new NotFoundException("Could not find expense with id: " + expenseId));

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

      if (expenseRequestDTO.type() != null) {
        expense.setType(expenseRequestDTO.type());
      }

      if (expenseRequestDTO.categoryID() != null) {
        Category category =
            categoryRepository
                .findById(expenseRequestDTO.categoryID())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Could not find category with id: " + expenseRequestDTO.categoryID()));

        expense.setCategory(category);
      }

      log.info("Updating expense {}", expense);
      expenseRepository.save(expense);

      log.info("Successfully updated expense {}", expense);
      return GeneralResponseDTO.builder()
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

  /**
   * Deletes an existing expense by its id. It takes in the id of the expense to be deleted and
   * returns a {@link GeneralResponseDTO} indicating the status of the request. If the request is
   * successful, it returns a status of {@link HttpStatus#OK} and a message indicating that the
   * expense was successfully deleted. If the expense with the given id is not found, it throws a
   * {@link NotFoundException}. If the user is not authorized to delete the expense, it throws a
   * {@link NotAuthorizedException}. If any unexpected error occurs while trying to delete the
   * expense, it throws an {@link InternalServerErrorException}.
   *
   * @param id the id of the expense to be deleted
   * @return a {@link GeneralResponseDTO} indicating the status of the request
   * @throws NotFoundException if the expense with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to delete the expense
   * @throws InternalServerErrorException if any unexpected error occurs while trying to delete the
   *     expense
   */
  @Override
  public GeneralResponseDTO deleteExpense(Long id)
      throws InternalServerErrorException, NotAuthorizedException, NotFoundException {
    log.info("Received delete expense request for {}", id);
    try {
      User user = Objects.requireNonNull(GetLoggedInUserUtil.getUser());
      Expense expense =
          expenseRepository
              .findById(id)
              .orElseThrow(() -> new NotFoundException("Could not find expense with id:" + id));

      if (!Objects.equals(expense.getUser().getId(), user.getId())) {
        throw new NotAuthorizedException("You do not have permission to delete this expense");
      }

      log.info("Deleting expense {}", expense);
      expenseRepository.delete(expense);

      log.info("Deleted expense {}", expense);
      return GeneralResponseDTO.builder()
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

  /**
   * Converts a {@link Page} of {@link Expense} to a {@link Page} of {@link ExpenseResponseDTO}. It
   * takes in a {@link Page} of {@link Expense} and returns a {@link Page} of {@link
   * ExpenseResponseDTO} containing the same data.
   *
   * @param expenses the page of expenses to be converted
   * @return a page of expense response DTOs containing the same data
   */
  private Page<ExpenseResponseDTO> convertExpenseToExpenseDTO(Page<Expense> expenses) {
    return expenses.map(
        expense ->
            ExpenseResponseDTO.builder()
                .id(expense.getId())
                .date(expense.getDate())
                .amount(expense.getAmount())
                .type(expense.getType())
                .description(expense.getDescription())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getDisplayName())
                .build());
  }
}
