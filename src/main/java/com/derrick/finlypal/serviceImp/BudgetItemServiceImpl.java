package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.BudgetItemRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.ExpenseResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.entity.Budget;
import com.derrick.finlypal.entity.BudgetItem;
import com.derrick.finlypal.entity.Expense;
import com.derrick.finlypal.enums.BudgetItemStatus;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.BudgetItemRepository;
import com.derrick.finlypal.repository.BudgetRepository;
import com.derrick.finlypal.repository.ExpenseRepository;
import com.derrick.finlypal.service.BudgetItemService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
public class BudgetItemServiceImpl implements BudgetItemService {

  private final BudgetItemRepository budgetItemRepository;
  private final BudgetRepository budgetRepository;
  private final ExpenseRepository expenseRepository;

  @Override
  public GeneralResponseDTO createBudgetItems(List<BudgetItemRequestDTO> budgetItems, Long budgetId)
      throws BadRequestException,
          InternalServerErrorException,
          NotFoundException,
          NotAuthorizedException {
    log.info("Received request to create budget items for budget with id {}", budgetId);

    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
      Budget budget =
          budgetRepository
              .findById(budgetId)
              .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

      if (!Objects.equals(budget.getUser().getId(), userId)) {
        throw new NotAuthorizedException(
            "You are not authorized to create budget items for this budget");
      }

      if (budgetItems.isEmpty()) {
        throw new BadRequestException("No budget items were provided");
      }

      log.info("Creating budget items for budget with id {}", budgetId);

      budgetItems.forEach(
          budgetItemRequestDTO -> {
            budgetItemRepository.save(
                BudgetItem.builder()
                    .name(budgetItemRequestDTO.name())
                    .allocatedAmount(budgetItemRequestDTO.allocatedAmount())
                    .status(BudgetItemStatus.ON_TRACK)
                    .budget(budget)
                    .build());
          });

      return GeneralResponseDTO.builder()
          .status(HttpStatus.OK)
          .message(budgetItems.size() + " budget items successfully created")
          .build();

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while creating budget items for budget with id {}", budgetId, e);
      throw new InternalServerErrorException(
          "An error occurred while creating budget items for budget with id "
              + budgetId
              + ": "
              + e.getMessage());
    }
  }

  @Override
  public Page<BudgetItemResponseDTO> getBudgetItems(Long budgetId, int page, int pageSize)
      throws InternalServerErrorException, NotFoundException, NotAuthorizedException {
    try {
      Pageable pageable = PageRequest.of(page, pageSize);
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
      Budget budget =
          budgetRepository
              .findById(budgetId)
              .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

      if (!Objects.equals(budget.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to read this budget");
      }

      log.info("Getting budget items with id {}", budgetId);
      Page<BudgetItem> budgetItems = budgetItemRepository.findAllByBudgetId(budgetId, pageable);

      return budgetItems.map(
          budgetItem ->
              BudgetItemResponseDTO.builder()
                  .id(budgetItem.getId())
                  .name(budgetItem.getName())
                  .allocatedAmount(budgetItem.getAllocatedAmount())
                  .status(
                      getBudgetItemStatus(
                          getActualSpend(userId, budgetItem.getId()),
                          budgetItem.getAllocatedAmount()))
                  .createdAt(budgetItem.getCreatedAt().toLocalDateTime().toLocalDate())
                  .actualSpend(getActualSpend(userId, budgetItem.getId()))
                  .build());

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while getting budget items for budget with id {}", budgetId, e);
      throw new InternalServerErrorException(
          "An error occurred while getting budget items for budget with id "
              + budgetId
              + ": "
              + e.getMessage());
    }
  }

  @Override
  public BudgetItemResponseDTO getBudgetItemById(Long budgetItemId)
      throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      BudgetItem budgetItem =
          budgetItemRepository
              .findById(budgetItemId)
              .orElseThrow(
                  () -> new NotFoundException("Budget item not found with id: " + budgetItemId));

      if (!Objects.equals(budgetItem.getBudget().getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to read this budget item");
      }

      BigDecimal actualSpend = getActualSpend(userId, budgetItemId);
      BigDecimal allocatedAmount = budgetItem.getAllocatedAmount();
      BudgetItemStatus budgetItemStatus = getBudgetItemStatus(actualSpend, allocatedAmount);
      List<ExpenseResponseDTO> expenses = getExpensesDTO(userId, budgetItemId);

      return BudgetItemResponseDTO.builder()
          .id(budgetItem.getId())
          .name(budgetItem.getName())
          .allocatedAmount(budgetItem.getAllocatedAmount())
          .status(budgetItemStatus)
          .createdAt(budgetItem.getCreatedAt().toLocalDateTime().toLocalDate())
          .actualSpend(getActualSpend(userId, budgetItem.getId()))
          .expenses(expenses)
          .build();

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while getting budget item with id {}", budgetItemId, e);
      throw new InternalServerErrorException(
          "An error occurred while getting budget item with id "
              + budgetItemId
              + ": "
              + e.getMessage());
    }
  }

  @Override
  public BudgetItemResponseDTO updateBudgetItem(
      Long budgetItemId, BudgetItemRequestDTO budgetItemRequestDTO)
      throws BadRequestException,
          NotFoundException,
          InternalServerErrorException,
          NotAuthorizedException {
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
      BudgetItem budgetItem =
          budgetItemRepository
              .findById(budgetItemId)
              .orElseThrow(
                  () -> new NotFoundException("Budget item not found with id: " + budgetItemId));

      if (!Objects.equals(budgetItem.getBudget().getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to update this budget item");
      }

      Budget budget =
          budgetRepository
              .findById(budgetItemRequestDTO.budgetId())
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "Budget not found with id: " + budgetItemRequestDTO.budgetId()));

      budgetItem.setName(budgetItemRequestDTO.name());
      budgetItem.setAllocatedAmount(budgetItemRequestDTO.allocatedAmount());
      budgetItem.setBudget(budget);

      BigDecimal actualSpend = getActualSpend(userId, budgetItemId);
      BigDecimal allocatedAmount = budgetItemRequestDTO.allocatedAmount();
      BudgetItemStatus budgetItemStatus = getBudgetItemStatus(actualSpend, allocatedAmount);

      return BudgetItemResponseDTO.builder()
          .id(budgetItem.getId())
          .name(budgetItem.getName())
          .allocatedAmount(budgetItem.getAllocatedAmount())
          .status(budgetItemStatus)
          .createdAt(budgetItem.getCreatedAt().toLocalDateTime().toLocalDate())
          .actualSpend(getActualSpend(userId, budgetItem.getId()))
          .build();

    } catch (BadRequestException | NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while updating budget item with id {}", budgetItemId, e);
      throw new InternalServerErrorException(
          "An error occurred while updating budget item with id "
              + budgetItemId
              + ": "
              + e.getMessage());
    }
  }

  @Override
  public GeneralResponseDTO deleteBudgetItem(Long budgetItemId)
      throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
    log.info("Received request to delete budget item with id {}", budgetItemId);

    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      BudgetItem budgetItem =
          budgetItemRepository
              .findById(budgetItemId)
              .orElseThrow(
                  () -> new NotFoundException("Budget item not found with id: " + budgetItemId));

      if (!Objects.equals(budgetItem.getBudget().getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to delete this budget item");
      }

      budgetItemRepository.delete(budgetItem);

      return GeneralResponseDTO.builder()
          .status(HttpStatus.OK)
          .message("Budget item with id " + budgetItemId + " deleted successfully")
          .build();
    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error occurred while deleting budget item with id {}", budgetItemId, e);
      throw new InternalServerErrorException(
          "An error occurred while deleting budget item with id "
              + budgetItemId
              + ": "
              + e.getMessage());
    }
  }

  private BudgetItemStatus getBudgetItemStatus(BigDecimal actualSpend, BigDecimal allocatedAmount) {
    if (actualSpend.compareTo(allocatedAmount) > 0) {
      return BudgetItemStatus.OVERSPENT;
    } else if (actualSpend.compareTo(allocatedAmount) < 0) {
      return BudgetItemStatus.UNDERSPENT;
    }
    return BudgetItemStatus.ON_TRACK;
  }

  private BigDecimal getActualSpend(Long userId, Long budgetItemId) {
    return expenseRepository.findAllByUserIdAndBudgetItemId(userId, budgetItemId).stream()
        .map(Expense::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private List<ExpenseResponseDTO> getExpensesDTO(Long userId, Long budgetItemId) {
    List<Expense> expenses = expenseRepository.findAllByUserIdAndBudgetItemId(userId, budgetItemId);

    return expenses.stream()
        .map(
            expense ->
                ExpenseResponseDTO.builder()
                    .id(expense.getId())
                    .date(expense.getDate())
                    .amount(expense.getAmount())
                    .type(expense.getType())
                    .description(expense.getDescription())
                    .categoryId(expense.getCategory().getId())
                    .categoryName(expense.getCategory().getDisplayName())
                    .build())
        .collect(Collectors.toList());
  }
}
