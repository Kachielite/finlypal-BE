package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.ExpenseResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.SavingsRequestDTO;
import com.derrick.finlypal.dto.SavingsResponseDTO;
import com.derrick.finlypal.entity.Expense;
import com.derrick.finlypal.entity.Savings;
import com.derrick.finlypal.enums.SavingsStatus;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.ExpenseRepository;
import com.derrick.finlypal.repository.SavingsRepository;
import com.derrick.finlypal.service.SavingsService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsServiceImpl implements SavingsService {
  private final SavingsRepository savingsRepository;
  private final ExpenseRepository expenseRepository;

  @Override
  public SavingsResponseDTO createSavings(SavingsRequestDTO savingsRequestDTO)
      throws BadRequestException, InternalServerErrorException {
    log.info("Received request to create savings: {}", savingsRequestDTO);
    try {
      Long userId = GetLoggedInUserUtil.getUser().getId();

      if (savingsRequestDTO.startDate().isAfter(savingsRequestDTO.endDate())) {
        throw new BadRequestException("Start date must be before end date");
      }

      if (savingsRequestDTO.startDate().isBefore(LocalDate.now())
          && savingsRequestDTO.endDate().isBefore(LocalDate.now())) {
        throw new BadRequestException("Start date and end date cannot be in the past");
      }

      log.info("Creating savings for user with id: {}", userId);

      Savings savings =
          Savings.builder()
              .goalName(savingsRequestDTO.goalName())
              .targetAmount(savingsRequestDTO.targetAmount())
              .savedAmount(BigDecimal.ZERO)
              .startDate(savingsRequestDTO.startDate())
              .endDate(savingsRequestDTO.endDate())
              .user(GetLoggedInUserUtil.getUser())
              .createdAt(
                  new Timestamp(
                      System.currentTimeMillis())) // ToDO: fix the bug of createdAt not set when
              // saving
              .status(
                  getSavingsStatus(
                      savingsRequestDTO.endDate(),
                      savingsRequestDTO.targetAmount(),
                      BigDecimal.ZERO))
              .build();

      // save the savings
      savingsRepository.save(savings);

      return SavingsResponseDTO.builder()
          .id(savings.getId())
          .goalName(savings.getGoalName())
          .targetAmount(savings.getTargetAmount())
          .savedAmount(savings.getSavedAmount())
          .startDate(savings.getStartDate().toString())
          .endDate(savings.getEndDate().toString())
          .status(savings.getStatus())
          .createdAt(savings.getCreatedAt().toLocalDateTime().toLocalDate())
          .build();

    } catch (BadRequestException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error creating savings goal: {}", e.getMessage());
      throw new InternalServerErrorException("Error creating savings goal: " + e.getMessage());
    }
  }

  @Override
  public SavingsResponseDTO updateSavings(Long savingsId, SavingsRequestDTO savingsRequestDTO)
      throws BadRequestException,
          NotFoundException,
          NotAuthorizedException,
          InternalServerErrorException {
    log.info("Received request to update savings: {}", savingsRequestDTO);
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Savings savings =
          savingsRepository
              .findById(savingsId)
              .orElseThrow(
                  () -> new NotFoundException("Savings goal not found with id: " + savingsId));

      if (!Objects.equals(savings.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to update this savings goal");
      }

      if (savingsRequestDTO.startDate().isAfter(savingsRequestDTO.endDate())) {
        throw new BadRequestException("Start date must be before end date");
      }

      if (savingsRequestDTO.startDate().isBefore(LocalDate.now())
          && savingsRequestDTO.endDate().isBefore(LocalDate.now())) {
        throw new BadRequestException("Start date and end date cannot be in the past");
      }

      if (savings.getStatus().equals(SavingsStatus.ACHIEVED)) {
        throw new BadRequestException("You cannot update an Achieved Savings Goals");
      }

      log.info("Updating savings for user with id: {}", userId);
      BigDecimal savedAmount = calculateSavedAmount(savingsId);

      savings.setGoalName(savingsRequestDTO.goalName());
      savings.setTargetAmount(savingsRequestDTO.targetAmount());
      savings.setStartDate(savingsRequestDTO.startDate());
      savings.setEndDate(savingsRequestDTO.endDate());
      savings.setSavedAmount(savedAmount);
      savings.setStatus(
          getSavingsStatus(
              savingsRequestDTO.endDate(), savingsRequestDTO.targetAmount(), savedAmount));

      // save the savings
      log.info("Saving savings for user with id: {}", userId);
      savingsRepository.save(savings);

      return SavingsResponseDTO.builder()
          .id(savings.getId())
          .goalName(savings.getGoalName())
          .targetAmount(savings.getTargetAmount())
          .savedAmount(savings.getSavedAmount())
          .startDate(savings.getStartDate().toString())
          .endDate(savings.getEndDate().toString())
          .status(savings.getStatus())
          .createdAt(savings.getCreatedAt().toLocalDateTime().toLocalDate())
          .build();

    } catch (BadRequestException | NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error updating savings goal: {}", e.getMessage());
      throw new InternalServerErrorException("Error updating savings goals: " + e.getMessage());
    }
  }

  @Override
  public SavingsResponseDTO getSavingsById(Long savingsId)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
    log.info("Received request to get savings by id: {}", savingsId);
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Savings savings =
          savingsRepository
              .findById(savingsId)
              .orElseThrow(
                  () -> new NotFoundException("Savings goal not found with id: " + savingsId));

      if (!Objects.equals(savings.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to read this savings goal");
      }

      BigDecimal savedAmount = calculateSavedAmount(savingsId);
      SavingsStatus status =
          getSavingsStatus(savings.getEndDate(), savings.getTargetAmount(), savedAmount);

      // Update savings status, saved amount
      savings.setStatus(status);
      savings.setSavedAmount(savedAmount);
      savingsRepository.save(savings);

      List<Expense> expenses = expenseRepository.findAllByUserIdAndSavingsId(userId, savingsId);

      List<ExpenseResponseDTO> expenseResponseDTOs =
          expenses.stream()
              .map(
                  expense ->
                      ExpenseResponseDTO.builder()
                          .id(expense.getId())
                          .amount(expense.getAmount())
                          .description(expense.getDescription())
                          .date(expense.getDate())
                          .type(expense.getType())
                          .categoryName(expense.getCategory().getName())
                          .categoryId(expense.getCategory().getId())
                          .build())
              .toList();

      return SavingsResponseDTO.builder()
          .id(savings.getId())
          .goalName(savings.getGoalName())
          .targetAmount(savings.getTargetAmount())
          .savedAmount(savedAmount)
          .startDate(savings.getStartDate().toString())
          .endDate(savings.getEndDate().toString())
          .status(savings.getStatus())
          .expenses(expenseResponseDTOs)
          .createdAt(savings.getCreatedAt().toLocalDateTime().toLocalDate())
          .build();

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error getting savings goal: {}", e.getMessage());
      throw new InternalServerErrorException("Error getting savings goal: " + e.getMessage());
    }
  }

  @Override
  public Page<SavingsResponseDTO> getAllSavings(int page, int pageSize)
      throws InternalServerErrorException {
    log.info("Received request to get all savings");
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Pageable pageable = PageRequest.of(page, pageSize);

      Page<Savings> savingsPage = savingsRepository.findAllByUserId(userId, pageable);

      // Update status and saved amount, then save
      savingsPage
          .getContent()
          .forEach(
              savings -> {
                BigDecimal savedAmount = calculateSavedAmount(savings.getId());
                SavingsStatus status =
                    getSavingsStatus(savings.getEndDate(), savings.getTargetAmount(), savedAmount);
                savings.setStatus(status);
                savings.setSavedAmount(savedAmount);
                savingsRepository.save(savings);
              });

      return savingsPage.map(
          savings ->
              SavingsResponseDTO.builder()
                  .id(savings.getId())
                  .goalName(savings.getGoalName())
                  .targetAmount(savings.getTargetAmount())
                  .savedAmount(savings.getSavedAmount())
                  .startDate(savings.getStartDate().toString())
                  .endDate(savings.getEndDate().toString())
                  .status(savings.getStatus())
                  .createdAt(savings.getCreatedAt().toLocalDateTime().toLocalDate())
                  .build());

    } catch (Exception e) {
      log.error("Error getting savings goals: {}", e.getMessage());
      throw new InternalServerErrorException("Error getting savings goal: " + e.getMessage());
    }
  }

  @Override
  public GeneralResponseDTO deleteSavings(Long savingsId)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
    log.info("Received request to delete savings goal: {}", savingsId);
    try {
      Long userId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

      Savings savings =
          savingsRepository
              .findById(savingsId)
              .orElseThrow(
                  () -> new NotFoundException("Savings goal not found with id: " + savingsId));

      if (!Objects.equals(savings.getUser().getId(), userId)) {
        throw new NotAuthorizedException("You are not authorized to delete this savings goal");
      }

      savingsRepository.delete(savings);

      return GeneralResponseDTO.builder().message("Savings goal deleted successfully").build();

    } catch (NotFoundException | NotAuthorizedException e) {
      log.error(e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error deleting savings goal: {}", e.getMessage());
      throw new InternalServerErrorException("Error deleting savings goal: " + e.getMessage());
    }
  }

  private SavingsStatus getSavingsStatus(
      LocalDate endDate, BigDecimal targetAmount, BigDecimal savedAmount) {
    LocalDate today = LocalDate.now();

    if (savedAmount.compareTo(targetAmount) >= 0) {
      return SavingsStatus.ACHIEVED;
    }

    if (endDate.isBefore(today)) {
      return SavingsStatus.FAILED;
    }

    return SavingsStatus.ON_TRACK;
  }

  private BigDecimal calculateSavedAmount(Long savingsId) {
    return expenseRepository.getTotalExpenseBySavingsId(savingsId);
  }
}
