package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.BudgetRequestDTO;
import com.derrick.finlypal.dto.BudgetResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.entity.Budget;
import com.derrick.finlypal.entity.BudgetItem;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.enums.BudgetItemStatus;
import com.derrick.finlypal.enums.BudgetStatus;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.BudgetItemRepository;
import com.derrick.finlypal.repository.BudgetRepository;
import com.derrick.finlypal.repository.ExpenseRepository;
import com.derrick.finlypal.service.BudgetService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {
  private final BudgetRepository budgetRepository;
  private final BudgetItemRepository budgetItemRepository;
  private final ExpenseRepository expenseRepository;
  private final BudgetItemServiceImpl budgetItemServiceImpl;

  @Override
  public BudgetResponseDTO createBudget(BudgetRequestDTO budgetRequestDTO)
      throws BadRequestException, InternalServerErrorException {
    log.info("Received request to create budget {}", budgetRequestDTO);

    try {
      User loggedInUser = Objects.requireNonNull(GetLoggedInUserUtil.getUser());

      if (budgetRequestDTO.startDate().isAfter(budgetRequestDTO.endDate())) {
        throw new BadRequestException("Start date must be before end date");
      }

      if (budgetRequestDTO.startDate().isBefore(LocalDate.now())
          && budgetRequestDTO.endDate().isBefore(LocalDate.now())) {
        throw new BadRequestException("Start date and end date cannot be in the past");
      }

      log.info("Creating new budget");
      Budget newBudget =
          Budget.builder()
              .name(budgetRequestDTO.budgetName())
              .startDate(budgetRequestDTO.startDate())
              .endDate(budgetRequestDTO.endDate())
              .totalBudget(budgetRequestDTO.totalBudget())
              .status(
                  getBudgetStatus(
                      budgetRequestDTO.startDate(),
                      budgetRequestDTO.endDate(),
                      Optional.of(budgetRequestDTO.totalBudget()),
                      Optional.empty()))
              .user(loggedInUser)
              .build();

      log.info("Saving new budget");
      Budget budget = budgetRepository.save(newBudget);

      return BudgetResponseDTO.builder()
          .id(budget.getId())
          .name(budget.getName())
          .startDate(budget.getStartDate())
          .endDate(budget.getEndDate())
          .totalBudget(budget.getTotalBudget())
          .status(budget.getStatus().name())
          .statusTooltip(getStatusTooltip(budget.getStatus()))
          .budgetItems(null)
          .createdAt(budget.getCreatedAt().toLocalDateTime().toLocalDate())
          .build();

    } catch (BadRequestException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while creating budget", e);
      throw new InternalServerErrorException(
          "An error occurred while creating the budget: " + e.getMessage());
    }
  }

  @Override
  public BudgetResponseDTO updateBudget(Long budgetId, BudgetRequestDTO budgetRequestDTO)
      throws BadRequestException,
          NotFoundException,
          NotAuthorizedException,
          InternalServerErrorException {
    log.info("Received request to update budget {}", budgetId);

    try {

      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Budget budget =
          budgetRepository
              .findById(budgetId)
              .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

      if (!Objects.equals(budget.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to update this budget");
      }

      if (budgetRequestDTO.startDate().isAfter(budgetRequestDTO.endDate())) {
        throw new BadRequestException("Start date must be before end date");
      }

      if (budgetRequestDTO.startDate().isBefore(LocalDate.now())
          && budgetRequestDTO.endDate().isBefore(LocalDate.now())) {
        throw new BadRequestException("Start date and end date cannot be in the past");
      }

      if (budget.getStatus() == BudgetStatus.COMPLETED) {
        throw new BadRequestException("Completed budgets cannot be updated");
      }

      log.info("Updating budget");
      budget.setName(budgetRequestDTO.budgetName());
      budget.setStartDate(budgetRequestDTO.startDate());
      budget.setEndDate(budgetRequestDTO.endDate());
      budget.setTotalBudget(budgetRequestDTO.totalBudget());
      budget.setStatus(
          getBudgetStatus(
              budgetRequestDTO.startDate(),
              budgetRequestDTO.endDate(),
              Optional.of(budgetRequestDTO.totalBudget()),
              Optional.of(calculateTotalSpent(budgetId))));

      log.info("Saving updated budget");
      Budget updatedBudget = budgetRepository.save(budget);

      return BudgetResponseDTO.builder()
          .id(updatedBudget.getId())
          .name(updatedBudget.getName())
          .startDate(updatedBudget.getStartDate())
          .endDate(updatedBudget.getEndDate())
          .totalBudget(updatedBudget.getTotalBudget())
          .status(updatedBudget.getStatus().name())
          .statusTooltip(getStatusTooltip(budget.getStatus()))
          .createdAt(updatedBudget.getCreatedAt().toLocalDateTime().toLocalDate())
          .build();

    } catch (BadRequestException | NotAuthorizedException | NotFoundException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while updating budget", e);
      throw new InternalServerErrorException(
          "An error occurred while updating the budget: " + e.getMessage());
    }
  }

  @Override
  public BudgetResponseDTO getBudgetById(Long budgetId)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
    log.info("Received request to get budget for id {}", budgetId);

    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Budget budget =
          budgetRepository
              .findById(budgetId)
              .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

      if (!Objects.equals(budget.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to read this budget");
      }

      log.info("Getting budget items with id {}", budgetId);
      List<BudgetItem> budgetItems = budgetItemRepository.findAllByBudgetId(budgetId);

      // Update the status for budget
      budget.setStatus(
          getBudgetStatus(
              budget.getStartDate(),
              budget.getEndDate(),
              Optional.of(budget.getTotalBudget()),
              Optional.of(calculateTotalSpent(budget.getId()))));

      // Save the status for budget
      budgetRepository.save(budget);

      List<BudgetItemResponseDTO> budgetItemResponseDTO =
          budgetItems.stream()
              .map(
                  budgetItem ->
                      BudgetItemResponseDTO.builder()
                          .id(budgetItem.getId())
                          .name(budgetItem.getName())
                          .allocatedAmount(budgetItem.getAllocatedAmount())
                          .actualSpend(
                              budgetItemServiceImpl.getActualSpend(userId, budgetItem.getId()))
                          .status(BudgetItemStatus.valueOf(budgetItem.getStatus().name()))
                          .createdAt(budgetItem.getCreatedAt().toLocalDateTime().toLocalDate())
                          .build())
              .toList();

      return BudgetResponseDTO.builder()
          .id(budget.getId())
          .name(budget.getName())
          .startDate(budget.getStartDate())
          .endDate(budget.getEndDate())
          .totalBudget(budget.getTotalBudget())
          .status(budget.getStatus().name())
          .budgetItems(budgetItemResponseDTO)
          .createdAt(budget.getCreatedAt().toLocalDateTime().toLocalDate())
          .build();

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while getting budget for id {}", budgetId, e);
      throw new InternalServerErrorException(
          "An error occurred while getting the budget: " + e.getMessage());
    }
  }

  @Override
  public Page<BudgetResponseDTO> getAllBudgets(int page, int pageSize)
      throws InternalServerErrorException {
    log.info("Received request to get all budgets");
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Pageable pageable = PageRequest.of(page, pageSize);

      log.info("Fetching budget list for user with id: {}", userId);
      Page<Budget> budgetLists = budgetRepository.findAllByUserId(userId, pageable);

      // Update the status for each budget
      budgetLists
          .getContent()
          .forEach(
              budget -> {
                budget.setStatus(
                    getBudgetStatus(
                        budget.getStartDate(),
                        budget.getEndDate(),
                        Optional.of(budget.getTotalBudget()),
                        Optional.of(calculateTotalSpent(budget.getId()))));
              });

      // Save the status for each budget
      budgetRepository.saveAll(budgetLists.getContent());

      return budgetLists.map(
          budget ->
              BudgetResponseDTO.builder()
                  .id(budget.getId())
                  .name(budget.getName())
                  .startDate(budget.getStartDate())
                  .endDate(budget.getEndDate())
                  .totalBudget(budget.getTotalBudget())
                  .status(budget.getStatus().name())
                  .budgetItems(null)
                  .createdAt(budget.getCreatedAt().toLocalDateTime().toLocalDate())
                  .build());

    } catch (Exception e) {
      log.info("Error occurred while getting all budgets", e);
      throw new InternalServerErrorException(
          "An error occurred while getting all budgets: " + e.getMessage());
    }
  }

  @Override
  public GeneralResponseDTO deleteBudget(Long budgetId)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
    log.info("Received request to delete budget with id {}", budgetId);

    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Budget budget =
          budgetRepository
              .findById(budgetId)
              .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

      if (!Objects.equals(budget.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to delete this budget");
      }

      budgetRepository.deleteById(budgetId);

      return GeneralResponseDTO.builder()
          .status(HttpStatus.OK)
          .message("Budget successfully deleted")
          .build();

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while deleting budget with id {}", budgetId, e);
      throw new InternalServerErrorException(
          "An error occurred while deleting budget: " + e.getMessage());
    }
  }

  private BigDecimal calculateTotalSpent(Long budgetId) {
    List<BudgetItem> budgetItems = budgetItemRepository.findAllByBudgetId(budgetId);

    BigDecimal totalExpense = BigDecimal.ZERO;

    for (BudgetItem item : budgetItems) {
      totalExpense = totalExpense.add(expenseRepository.getTotalExpenseByBudgetItem(item.getId()));
    }

    return totalExpense;
  }

  private BudgetStatus getBudgetStatus(
      LocalDate startDate,
      LocalDate endDate,
      Optional<BigDecimal> totalBudget,
      Optional<BigDecimal> totalSpent) {
    LocalDate today = LocalDate.now();

    // If the budget starts in the future → PLANNED
    if (startDate.isAfter(today)) {
      return BudgetStatus.PLANNED;
    }

    // If the budget period has ended
    if (endDate.isBefore(today)) {
      if (totalSpent.isPresent()
          && totalBudget.isPresent()
          && totalSpent.get().compareTo(totalBudget.get()) > 0) {
        return BudgetStatus.EXCEEDED; // Budget is over the limit
      }
      return BudgetStatus.EXPIRED; // Budget ended but within limits
    }

    // Otherwise, it's in progress
    return BudgetStatus.IN_PROGRESS;
  }

  private String getStatusTooltip(BudgetStatus status) {
    return switch (status) {
      case PLANNED -> "This budget is set for a future period and hasn't started yet.";
      case IN_PROGRESS -> "This budget is currently active. You can track spending in real-time.";
      case EXCEEDED -> "This budget has ended, and spending went over the allocated amount.";
      case EXPIRED ->
          "This budget has ended, but spending remained within the allocated amount. Mark it as completed if you're done with it.";
      case COMPLETED -> "You have manually marked this budget as completed.";
    };
  }
}
