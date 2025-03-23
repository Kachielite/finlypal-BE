package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.BudgetItemCreateRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.BudgetItemUpdateRequestDTO;
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
import jakarta.transaction.Transactional;
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

  /**
   * Creates multiple budget items for a specified budget.
   *
   * <p>This method takes a list of {@link BudgetItemCreateRequestDTO} and a budget ID. It checks if
   * the current user is authorized to create items for the specified budget. If authorized, it
   * creates and saves the budget items in the repository. If the budget is not found, or the user
   * is not authorized, or no budget items are provided, it throws the respective exceptions.
   *
   * @param budgetItems the list of budget items to be created
   * @param budgetId the ID of the budget for which the items are to be created
   * @return a {@link GeneralResponseDTO} indicating the status of the operation
   * @throws BadRequestException if no budget items are provided
   * @throws InternalServerErrorException if any unexpected error occurs during the process
   * @throws NotFoundException if the budget with the given ID is not found
   * @throws NotAuthorizedException if the user is not authorized to create budget items for the
   *     budget
   */
  @Transactional
  @Override
  public GeneralResponseDTO createBudgetItems(
      List<BudgetItemCreateRequestDTO> budgetItems, Long budgetId)
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

      budgetItems.forEach(
          budgetItemRequestDTO -> {
            budgetItemRepository.save(
                BudgetItem.builder()
                    .name(budgetItemRequestDTO.name())
                    .icon(budgetItemRequestDTO.icon())
                    .allocatedAmount(budgetItemRequestDTO.allocatedAmount())
                    .status(BudgetItemStatus.ON_TRACK)
                    .budget(budget)
                    .build());
          });

      // check that the sum of budget items is not greater than the budget amount
      // 1. sum up the allocated amount of the budget items
      // 2. sum up the allocated amount of existing budget items belong to the given budget id
      // 3. Do a grand total sum for both 1 and 2
      // 4. update the totalBudget of the budget with the grand total sum and save it
      log.info("Updating total budget for budget with id {}", budgetId);
      BigDecimal totalBudget =
          budgetItems.stream()
              .map(BudgetItemCreateRequestDTO::allocatedAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      List<BudgetItem> existingBudgetItems = budgetItemRepository.findAllByBudgetId(budgetId);

      BigDecimal totalExistingBudget =
          existingBudgetItems.stream()
              .map(BudgetItem::getAllocatedAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal grandTotal = totalBudget.add(totalExistingBudget);

      budget.setTotalBudget(grandTotal);
      budgetRepository.save(budget);

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

  /**
   * Get all budget items associated with the given budget id. The budget items are paged and can be
   * filtered by the given page and page size.
   *
   * @param budgetId the id of the budget to get budget items from
   * @param page the page number to be returned
   * @param pageSize the number of items to be returned in each page
   * @return a page of budget items associated with the given budget id
   * @throws InternalServerErrorException if any unexpected error occurs while trying to get the
   *     budget items
   * @throws NotFoundException if the budget with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to read the budget
   */
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
                  .icon(budgetItem.getIcon())
                  .allocatedAmount(budgetItem.getAllocatedAmount())
                  .status(
                      getBudgetItemStatus(
                          getActualSpend(userId, budgetItem.getId()),
                          budgetItem.getAllocatedAmount()))
                  .createdAt(budgetItem.getCreatedAt())
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

  /**
   * This method is used to get a budget item by its id. It returns a {@link BudgetItemResponseDTO}
   * if the budget item is found, otherwise it throws a {@link NotFoundException}. It also checks if
   * the user is authorized to view the budget item by comparing the user id of the budget item and
   * the logged in user. If the user is not authorized, it throws a {@link NotAuthorizedException}.
   * If any unexpected error occurs while trying to get the budget item, it throws an {@link
   * InternalServerErrorException}.
   *
   * @param budgetItemId the id of the budget item to be found
   * @return the budget item found, represented as a {@link BudgetItemResponseDTO}
   * @throws NotFoundException if the budget item with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to view the budget item
   * @throws InternalServerErrorException if any unexpected error occurs while trying to get the
   *     budget item
   */
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
          .icon(budgetItem.getIcon())
          .allocatedAmount(budgetItem.getAllocatedAmount())
          .status(budgetItemStatus)
          .statusTooltip(getStatusTooltip(budgetItemStatus))
          .createdAt(budgetItem.getCreatedAt())
          .actualSpend(getActualSpend(userId, budgetItem.getId()))
          .expenses(expenses)
          .budgetId(budgetItem.getBudget().getId())
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

  /**
   * Updates an existing budget item with the given {@link BudgetItemUpdateRequestDTO}. It takes in
   * the id of the budget item to be updated and the {@link BudgetItemUpdateRequestDTO} containing
   * the new values. It returns a {@link BudgetItemResponseDTO} indicating the status of the
   * request. If the request is successful, it returns a status of {@link HttpStatus#OK} and a
   * message indicating that the budget item was successfully updated. If the budget item with the
   * given id is not found, it throws a {@link NotFoundException}. If the user is not authorized to
   * update the budget item, it throws a {@link NotAuthorizedException}. If any unexpected error
   * occurs while trying to update the budget item, it throws an {@link
   * InternalServerErrorException}.
   *
   * @param budgetItemId the id of the budget item to be updated
   * @param budgetItemRequestDTO the budget item request containing the new values
   * @return a {@link BudgetItemResponseDTO} indicating the status of the request
   * @throws BadRequestException if the request is invalid
   * @throws NotFoundException if the budget item with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to update the budget item
   * @throws InternalServerErrorException if any unexpected error occurs while trying to update the
   *     budget item
   */
  @Override
  public BudgetItemResponseDTO updateBudgetItem(
      Long budgetItemId, BudgetItemUpdateRequestDTO budgetItemRequestDTO)
      throws BadRequestException,
          NotFoundException,
          InternalServerErrorException,
          NotAuthorizedException {
    log.info("Updating budget item with id {} and request {}", budgetItemId, budgetItemRequestDTO);
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
      budgetItem.setIcon(budgetItemRequestDTO.icon());
      budgetItem.setAllocatedAmount(budgetItemRequestDTO.allocatedAmount());
      budgetItem.setBudget(budget);

      BigDecimal actualSpend = getActualSpend(userId, budgetItemId);
      BigDecimal allocatedAmount = budgetItemRequestDTO.allocatedAmount();
      BudgetItemStatus budgetItemStatus = getBudgetItemStatus(actualSpend, allocatedAmount);

      log.info("Updating budget item with id {}", budgetItemId);
      budgetItemRepository.save(budgetItem);

      log.info("Updating total budget for budget with id {}", budgetItemRequestDTO.budgetId());
      List<BudgetItem> existingBudgetItems =
          budgetItemRepository.findAllByBudgetId(budgetItemRequestDTO.budgetId());
      BigDecimal totalExistingBudget =
          existingBudgetItems.stream()
              .map(BudgetItem::getAllocatedAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal grandTotal = totalExistingBudget.add(budgetItemRequestDTO.allocatedAmount());
      budget.setTotalBudget(grandTotal);
      log.info("Saving budget with id {}", budgetItemRequestDTO.budgetId());
      budgetRepository.save(budget);

      return BudgetItemResponseDTO.builder()
          .id(budgetItem.getId())
          .name(budgetItem.getName())
          .icon(budgetItem.getIcon())
          .allocatedAmount(budgetItem.getAllocatedAmount())
          .status(budgetItemStatus)
          .statusTooltip(getStatusTooltip(budgetItemStatus))
          .createdAt(budgetItem.getCreatedAt())
          .actualSpend(getActualSpend(userId, budgetItem.getId()))
          .budgetId(budgetItem.getBudget().getId())
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

  /**
   * Deletes a budget item with the given id. It takes in the id of the budget item to be deleted
   * and returns a {@link GeneralResponseDTO} indicating the status of the request. If the budget
   * item with the given id is not found, it throws a {@link NotFoundException}. If the user is not
   * authorized to delete the budget item, it throws a {@link NotAuthorizedException}. If any
   * unexpected error occurs while trying to delete the budget item, it throws an {@link
   * InternalServerErrorException}.
   *
   * @param budgetItemId the id of the budget item to be deleted
   * @return a {@link GeneralResponseDTO} indicating the status of the request
   * @throws NotFoundException if the budget item with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to delete the budget item
   * @throws InternalServerErrorException if any unexpected error occurs while trying to delete the
   *     budget item
   */
  @Transactional
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

      // Update the total budget for the budget
      Long budgetID = budgetItem.getBudget().getId();
      Budget budget =
          budgetRepository
              .findById(budgetID)
              .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetID));

      List<BudgetItem> existingBudgetItems = budgetItemRepository.findAllByBudgetId(budgetID);
      BigDecimal totalExistingBudget =
          existingBudgetItems.stream()
              .map(BudgetItem::getAllocatedAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      BigDecimal grandTotal = totalExistingBudget.subtract(budgetItem.getAllocatedAmount());
      budget.setTotalBudget(grandTotal);
      budgetRepository.save(budget);

      // Delete the budget item
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

  /**
   * Determines the status of a budget item based on the actual spend and allocated amount.
   *
   * <p>The status is determined as follows:
   *
   * <ul>
   *   <li>If the actual spend is greater than the allocated amount, the status is {@link
   *       BudgetItemStatus#OVERSPENT}.
   *   <li>If the actual spend is less than the allocated amount, the status is {@link
   *       BudgetItemStatus#UNDERSPENT}.
   *   <li>If the actual spend is equal to the allocated amount, the status is {@link
   *       BudgetItemStatus#ON_TRACK}.
   * </ul>
   *
   * @param actualSpend the actual spend of the budget item
   * @param allocatedAmount the allocated amount of the budget item
   * @return the status of the budget item
   */
  private BudgetItemStatus getBudgetItemStatus(BigDecimal actualSpend, BigDecimal allocatedAmount) {
    BigDecimal threshold = allocatedAmount.multiply(BigDecimal.valueOf(0.9)); // 90% of allocation

    if (actualSpend.compareTo(allocatedAmount) > 0) {
      return BudgetItemStatus.OVERSPENT; // Spending exceeded allocation
    }
    if (actualSpend.compareTo(threshold) >= 0) {
      return BudgetItemStatus.AT_RISK; // Close to exceeding allocation
    }
    if (actualSpend.compareTo(allocatedAmount.multiply(BigDecimal.valueOf(0.5))) < 0) {
      return BudgetItemStatus.UNDERSPENT; // Less than 50% of allocation used
    }
    return BudgetItemStatus.ON_TRACK; // Spending is under control
  }

  private String getStatusTooltip(BudgetItemStatus status) {
    return switch (status) {
      case OVERSPENT -> "🚨 This budget category has exceeded its allocated amount!";
      case AT_RISK ->
          "⚠️ This budget category is close to exceeding its allocated amount. Monitor your spending!";
      case ON_TRACK -> "✅ This budget category is within the allocated budget and on track.";
      case UNDERSPENT ->
          "📉 Less than 50% of the allocated amount has been spent. Consider adjusting future allocations.";
    };
  }

  /**
   * Calculates the total actual spend for a specific budget item by a user.
   *
   * <p>This method retrieves all expenses associated with the given user ID and budget item ID,
   * sums up the amounts of these expenses, and returns the total spend.
   *
   * @param userId the ID of the user whose expenses are to be summed
   * @param budgetItemId the ID of the budget item for which the total spend is calculated
   * @return the total actual spend as a {@link BigDecimal}
   */
  public BigDecimal getActualSpend(Long userId, Long budgetItemId) {
    return expenseRepository.findAllByUserIdAndBudgetItemId(userId, budgetItemId).stream()
        .map(Expense::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Retrieves all expenses associated with a specific user ID and budget item ID, and converts them
   * to a list of {@link ExpenseResponseDTO}.
   *
   * @param userId the ID of the user whose expenses are to be retrieved
   * @param budgetItemId the ID of the budget item for which the expenses are retrieved
   * @return a list of {@link ExpenseResponseDTO} containing the expenses
   */
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
