package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.BudgetItemRequestDTO;
import com.derrick.finlypal.dto.BudgetItemResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.entity.Budget;
import com.derrick.finlypal.entity.BudgetItem;
import com.derrick.finlypal.enums.BudgetItemStatus;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.BudgetItemRepository;
import com.derrick.finlypal.repository.BudgetRepository;
import com.derrick.finlypal.service.BudgetItemService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetItemImpl implements BudgetItemService {

    private final BudgetItemRepository budgetItemRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public GeneralResponseDTO createBudgetItems(List<BudgetItemRequestDTO> budgetItems, Long budgetId) throws BadRequestException, InternalServerErrorException, NotFoundException, NotAuthorizedException {
        log.info("Received request to create budget items for budget with id {}", budgetId);

        try {
            Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();
            Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new NotFoundException("Budget not found with id: " + budgetId));

            if (!Objects.equals(budget.getUser().getId(), userId)) {
                throw new NotAuthorizedException("You are not authorized to create budget items for this budget");
            }

            if (budgetItems.isEmpty()) {
                throw new BadRequestException("No budget items were provided");
            }


            log.info("Creating budget items for budget with id {}", budgetId);

            budgetItems.forEach(budgetItemRequestDTO -> {
                budgetItemRepository.save(BudgetItem.builder()
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
                    "An error occurred while creating budget items for budget with id " + budgetId + ": " + e.getMessage());
        }
    }

    @Override
    public Page<BudgetItemResponseDTO> getBudgetItems(Long budgetId) throws InternalServerErrorException, NotFoundException, NotAuthorizedException {
        return null;
    }

    @Override
    public BudgetItemResponseDTO getBudgetItemById(Long budgetItemId) throws NotFoundException, InternalServerErrorException, NotAuthorizedException {
        return null;
    }

    @Override
    public BudgetItemResponseDTO updateBudgetItem(Long budgetItemId, BudgetItemRequestDTO budgetItemRequestDTO) throws BadRequestException, NotFoundException, InternalServerErrorException, NotAuthorizedException {
        return null;
    }

    @Override
    public void deleteBudgetItem(Long budgetItemId) throws NotFoundException, InternalServerErrorException, NotAuthorizedException {

    }

    private BudgetItemStatus getBudgetItemStatus(String status) {
        if (status.equals("ON_TRACK")) {
            return BudgetItemStatus.ON_TRACK;
        } else if (status.equals("OVERSPENT")) {
            return BudgetItemStatus.OVERSPENT;
        } else if (status.equals("UNDERSPENT")) {
            return BudgetItemStatus.UNDERSPENT;
        }

        return null;
    }
}
