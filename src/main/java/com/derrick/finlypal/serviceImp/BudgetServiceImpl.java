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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetItemServiceImpl budgetItemServiceImpl;

    /**
     * Creates a new budget for the logged-in user. It validates the provided budget details, such as
     * ensuring the start date is before the end date and that both dates are not in the past. If the
     * budget details are valid, it calculates the budget status and saves the budget to the
     * repository. Returns a BudgetResponseDTO containing the budget's details.
     *
     * @param budgetRequestDTO the budget request details
     * @return BudgetResponseDTO containing the created budget's details
     * @throws BadRequestException          if the budget details are invalid
     * @throws InternalServerErrorException if an unexpected error occurs during budget creation
     */
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
                            .icon(budgetRequestDTO.icon())
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
                    .icon(budget.getIcon())
                    .startDate(budget.getStartDate())
                    .endDate(budget.getEndDate())
                    .totalBudget(budget.getTotalBudget())
                    .actualSpend(BigDecimal.ZERO)
                    .status(budget.getStatus().name())
                    .statusTooltip(getStatusTooltip(budget.getStatus()))
                    .budgetItems(null)
                    .createdAt(budget.getCreatedAt())
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

    /**
     * Updates a budget with the given {@link BudgetRequestDTO}. It takes in the id of the budget to
     * be updated and the {@link BudgetRequestDTO} containing the new values. It returns a {@link
     * BudgetResponseDTO} indicating the status of the request. If the request is successful, it
     * returns a status of {@link HttpStatus#OK} and a message indicating that the budget was
     * successfully updated. If the budget with the given id is not found, it throws a {@link
     * NotFoundException}. If the user is not authorized to update the budget, it throws a {@link
     * NotAuthorizedException}. If any unexpected error occurs while trying to update the budget, it
     * throws an {@link InternalServerErrorException}.
     *
     * <p>It also validates that the start date is before the end date, and that the budget is not in
     * the past. If the budget is completed, it throws a {@link BadRequestException}.
     *
     * @param budgetId         the id of the budget to be updated
     * @param budgetRequestDTO the budget request containing the new values
     * @return a {@link BudgetResponseDTO} indicating the status of the request
     * @throws NotFoundException            if the budget with the given id is not found
     * @throws NotAuthorizedException       if the user is not authorized to update the budget
     * @throws BadRequestException          if the budget details are invalid
     * @throws InternalServerErrorException if an unexpected error occurs while trying to update the
     *                                      budget
     */
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

            // Calculate the total spent on the budget
            BigDecimal actualSpend = budgetRepository.findTotalExpensesByBudgetId(budgetId);

            log.info("Updating budget");
            budget.setName(budgetRequestDTO.budgetName());
            budget.setIcon(budgetRequestDTO.icon());
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
                    .icon(updatedBudget.getIcon())
                    .startDate(updatedBudget.getStartDate())
                    .endDate(updatedBudget.getEndDate())
                    .totalBudget(updatedBudget.getTotalBudget())
                    .actualSpend(actualSpend)
                    .status(updatedBudget.getStatus().name())
                    .statusTooltip(getStatusTooltip(budget.getStatus()))
                    .createdAt(updatedBudget.getCreatedAt())
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

    /**
     * Retrieves a budget by its ID for the logged-in user. This method ensures that the budget exists
     * and belongs to the logged-in user, throwing exceptions if these conditions are not met. It also
     * updates the budget's status based on its start and end dates and total spent amount. The
     * response includes the budget's details along with its associated budget items.
     *
     * @param budgetId the ID of the budget to be retrieved
     * @return a {@link BudgetResponseDTO} containing the budget's details and its items
     * @throws NotFoundException            if no budget is found with the specified ID
     * @throws NotAuthorizedException       if the logged-in user is not authorized to access the budget
     * @throws InternalServerErrorException if an unexpected error occurs during retrieval
     */
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

            budget.setStatus(
                    budget.getStatus() == BudgetStatus.COMPLETED
                            ? BudgetStatus.COMPLETED
                            : getBudgetStatus(
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
                                                    .icon(budgetItem.getIcon())
                                                    .allocatedAmount(budgetItem.getAllocatedAmount())
                                                    .actualSpend(
                                                            budgetItemServiceImpl.getActualSpend(userId, budgetItem.getId()))
                                                    .status(BudgetItemStatus.valueOf(budgetItem.getStatus().name()))
                                                    .createdAt(budgetItem.getCreatedAt())
                                                    .build())
                            .toList();

            // Calculate the total spent on the budget
            BigDecimal actualSpend = budgetRepository.findTotalExpensesByBudgetId(budgetId);

            return BudgetResponseDTO.builder()
                    .id(budget.getId())
                    .name(budget.getName())
                    .icon(budget.getIcon())
                    .startDate(budget.getStartDate())
                    .endDate(budget.getEndDate())
                    .totalBudget(budget.getTotalBudget())
                    .actualSpend(actualSpend)
                    .status(budget.getStatus().name())
                    .statusTooltip(getStatusTooltip(budget.getStatus()))
                    .budgetItems(budgetItemResponseDTO)
                    .createdAt(budget.getCreatedAt())
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

    /**
     * Returns a list of budgets for the currently logged in user.
     *
     * <p>This API returns a list of budgets for the currently logged in user. It validates that the
     * user is authenticated and returns a paginated list of budgets. The status of each budget is
     * updated based on the current date and the total amount spent.
     *
     * @param page     the page number to be returned
     * @param pageSize the number of items to be returned in each page
     * @return a page of budgets found, represented as a {@link BudgetResponseDTO}
     * @throws InternalServerErrorException if any unexpected error occurs while trying to find the
     *                                      budgets
     */
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
                            budget -> budget.setStatus(
                                    budget.getStatus() == BudgetStatus.COMPLETED
                                            ? BudgetStatus.COMPLETED
                                            : getBudgetStatus(
                                            budget.getStartDate(),
                                            budget.getEndDate(),
                                            Optional.of(budget.getTotalBudget()),
                                            Optional.of(calculateTotalSpent(budget.getId())))));

            // Save the status for each budget
            budgetRepository.saveAll(budgetLists.getContent());

            return budgetLists.map(
                    budget ->
                            BudgetResponseDTO.builder()
                                    .id(budget.getId())
                                    .name(budget.getName())
                                    .icon(budget.getIcon())
                                    .startDate(budget.getStartDate())
                                    .endDate(budget.getEndDate())
                                    .totalBudget(budget.getTotalBudget())
                                    .actualSpend(budgetRepository.findTotalExpensesByBudgetId(budget.getId()))
                                    .status(budget.getStatus().name())
                                    .statusTooltip(getStatusTooltip(budget.getStatus()))
                                    .createdAt(budget.getCreatedAt())
                                    .build());

        } catch (Exception e) {
            log.info("Error occurred while getting all budgets", e);
            throw new InternalServerErrorException(
                    "An error occurred while getting all budgets: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GeneralResponseDTO markBudgetAsCompleted(Long budgetId)
            throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
        log.info("Received request to mark budget as completed {}", budgetId);
        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            Budget budget =
                    budgetRepository
                            .findById(budgetId)
                            .orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

            if (!Objects.equals(budget.getUser().getId(), userId)) {
                throw new NotAuthorizedException("You are not authorized to update this budget");
            }

            if (BudgetStatus.COMPLETED.equals(budget.getStatus())) {
                throw new BadRequestException("Budget is already completed");
            }

            log.info("Setting budget status to COMPLETED");
            budget.setStatus(BudgetStatus.COMPLETED);
            budgetRepository.saveAndFlush(budget);
            log.info("Budget status after saving: {}", budget.getStatus());

            return GeneralResponseDTO.builder()
                    .status(HttpStatus.OK)
                    .message("Budget marked as completed successfully")
                    .build();

        } catch (NotFoundException | NotAuthorizedException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while marking budget as completed {}", budgetId, e);
            throw new InternalServerErrorException(
                    "An error occurred while marking budget as completed: " + e.getMessage());
        }
    }

    /**
     * Deletes a budget by its ID for the logged-in user. This method ensures that the budget exists
     * and belongs to the logged-in user, throwing exceptions if these conditions are not met. Upon
     * successful deletion, it returns a {@link GeneralResponseDTO} with a status of {@link
     * HttpStatus#OK} and a message indicating the successful deletion of the budget. If the budget
     * with the given ID is not found, it throws a {@link NotFoundException}. If the user is not
     * authorized to delete the budget, it throws a {@link NotAuthorizedException}. If any unexpected
     * error occurs while trying to delete the budget, it throws an {@link
     * InternalServerErrorException}.
     *
     * @param budgetId the ID of the budget to be deleted
     * @return a {@link GeneralResponseDTO} indicating the status of the request
     * @throws NotFoundException            if the budget with the given ID is not found
     * @throws NotAuthorizedException       if the user is not authorized to delete the budget
     * @throws InternalServerErrorException if any unexpected error occurs while trying to delete the
     *                                      budget
     */
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

    /**
     * Calculates the total amount of money spent in a budget by summing the total amounts of all
     * {@link BudgetItem}s associated with the budget. This method iterates over the budget items, and
     * for each item, it fetches the total amount of money spent using the {@link
     * ExpenseRepository#getTotalExpenseByBudgetItem(Long)} method. The total amount spent is then
     * returned as a {@link BigDecimal}.
     *
     * @param budgetId the ID of the budget
     * @return the total amount of money spent in the budget
     */
    private BigDecimal calculateTotalSpent(Long budgetId) {
        List<BudgetItem> budgetItems = budgetItemRepository.findAllByBudgetId(budgetId);

        BigDecimal totalExpense = BigDecimal.ZERO;

        for (BudgetItem item : budgetItems) {
            totalExpense = totalExpense.add(expenseRepository.getTotalExpenseByBudgetItem(item.getId()));
        }

        return totalExpense;
    }

    /**
     * Determines the status of a budget based on its start and end dates, as well as the total
     * budgeted amount and the amount spent so far. The budget can be in one of the following states:
     * PLANNED, EXCEEDED, EXPIRED, or IN_PROGRESS.
     *
     * <ul>
     *   <li>PLANNED: The budget is scheduled to start in the future.
     *   <li>EXCEEDED: The budget period has ended, and the total spent exceeds the allocated budget.
     *   <li>EXPIRED: The budget period has ended, but the total spent is within the allocated budget.
     *   <li>IN_PROGRESS: The budget is currently active.
     * </ul>
     *
     * @param startDate   the start date of the budget
     * @param endDate     the end date of the budget
     * @param totalBudget the total budgeted amount (optional)
     * @param totalSpent  the total amount spent so far (optional)
     * @return the current status of the budget
     */
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

        // If the budget is still active (today is before or equal to endDate)
        if (!endDate.isBefore(today) && totalSpent.isPresent() && totalBudget.isPresent()) {
            BigDecimal spent = totalSpent.get();
            BigDecimal budget = totalBudget.get();
            BigDecimal threshold = budget.multiply(BigDecimal.valueOf(0.9)); // 90% of budget

            if (spent.compareTo(budget) >= 0) {
                return BudgetStatus.EXCEEDED; // Budget is still active but fully used up
            }
            if (spent.compareTo(threshold) >= 0) {
                return BudgetStatus.AT_RISK; // Budget is close to being exceeded
            }
        }

        // If the budget period has ended
        if (endDate.isBefore(today) && totalSpent.isPresent() && totalBudget.isPresent()) {
            BigDecimal spent = totalSpent.get();
            BigDecimal budget = totalBudget.get();

            if (spent.compareTo(budget) > 0) {
                return BudgetStatus.EXCEEDED; // Budget was exceeded before it ended
            }
            if (spent.compareTo(budget.multiply(BigDecimal.valueOf(0.5))) < 0) {
                return BudgetStatus.UNDERUTILIZED; // Less than 50% of budget used
            }
            return BudgetStatus.EXPIRED; // Budget ended but within limits
        }

        return BudgetStatus.IN_PROGRESS; // Budget is ongoing and within limits
    }

    /**
     * Returns a tooltip string describing the given budget status.
     *
     * @param status the budget status
     * @return a string describing the budget status
     */
    private String getStatusTooltip(BudgetStatus status) {
        return switch (status) {
            case PLANNED -> "📅 This budget is set for a future period and hasn't started yet.";
            case IN_PROGRESS -> "⏳ This budget is currently active. You can track spending in real-time.";
            case EXCEEDED -> "🚨 This budget is still active, but the allocated amount has already been used up!";
            case EXPIRED ->
                    "✅ This budget has ended, but spending remained within the allocated amount. Mark it as completed if you're done with it.";
            case COMPLETED -> "🎉 You have manually marked this budget as completed.";
            case AT_RISK -> "⚠️ Spending is close to exceeding the budget. Monitor carefully!";
            case UNDERUTILIZED -> "📉 Less than 50% of the budget was used. Consider adjusting future allocations.";
        };
    }
}
