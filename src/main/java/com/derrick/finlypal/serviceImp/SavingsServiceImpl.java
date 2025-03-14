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

  /**
   * Creates a new savings goal for the logged-in user. It validates the provided savings details,
   * such as ensuring the start date is before the end date and that both dates are not in the past.
   * If the savings details are valid, it calculates the savings status and saves the savings to the
   * repository. Returns a SavingsResponseDTO containing the savings details.
   *
   * @param savingsRequestDTO the savings request details
   * @return SavingsResponseDTO containing the created savings's details
   * @throws BadRequestException if the savings details are invalid
   * @throws InternalServerErrorException if an unexpected error occurs during savings creation
   */
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

  /**
   * Updates an existing savings goal with the given {@link SavingsRequestDTO}. It takes in the id
   * of the savings goal to be updated and the {@link SavingsRequestDTO} containing the new values.
   * It returns a {@link SavingsResponseDTO} indicating the status of the request. If the request is
   * successful, it returns a status of {@link HttpStatus#OK} and a message indicating that the
   * savings goal was successfully updated. If the savings goal with the given id is not found, it
   * throws a {@link NotFoundException}. If the user is not authorized to update the savings goal,
   * it throws a {@link NotAuthorizedException}. If any unexpected error occurs while trying to
   * update the savings goal, it throws an {@link InternalServerErrorException}.
   *
   * @param savingsId the id of the savings goal to be updated
   * @param savingsRequestDTO the savings request containing the new values
   * @return a {@link SavingsResponseDTO} indicating the status of the request
   * @throws BadRequestException if the request is invalid
   * @throws NotFoundException if the savings goal with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to update the savings goal
   * @throws InternalServerErrorException if any unexpected error occurs while trying to update the
   *     savings goal
   */
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
          .statusTooltip(getSavingsStatusTooltip(savings.getStatus()))
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

  /**
   * Retrieves a savings goal by its id. It first checks if the logged-in user is authorized to read
   * the savings goal. If the user is not authorized, it throws a {@link NotAuthorizedException}. If
   * the savings goal with the given id is not found, it throws a {@link NotFoundException}. If any
   * unexpected error occurs while trying to retrieve the savings goal, it throws an {@link
   * InternalServerErrorException}.
   *
   * @param savingsId the id of the savings goal to be retrieved
   * @return a {@link SavingsResponseDTO} containing the retrieved savings goal's details
   * @throws NotFoundException if the savings goal with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to read the savings goal
   * @throws InternalServerErrorException if any unexpected error occurs while trying to retrieve
   *     the savings goal
   */
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
          .statusTooltip(getSavingsStatusTooltip(savings.getStatus()))
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

  /**
   * Retrieves a paginated list of savings goals for the currently logged-in user.
   *
   * <p>This method fetches all savings goals associated with the logged-in user, updates the status
   * and saved amount for each savings goal, and returns them as a page of {@link
   * SavingsResponseDTO}. It ensures that the user is authenticated and handles any unexpected
   * errors during the process.
   *
   * @param page the page number to be retrieved
   * @param pageSize the number of items to be included in each page
   * @return a page of savings goals represented as {@link SavingsResponseDTO}
   * @throws InternalServerErrorException if any unexpected error occurs while retrieving the
   *     savings goals
   */
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
                  .statusTooltip(getSavingsStatusTooltip(savings.getStatus()))
                  .createdAt(savings.getCreatedAt().toLocalDateTime().toLocalDate())
                  .build());

    } catch (Exception e) {
      log.error("Error getting savings goals: {}", e.getMessage());
      throw new InternalServerErrorException("Error getting savings goal: " + e.getMessage());
    }
  }

  /**
   * Deletes a savings goal with the given id.
   *
   * <p>This method validates the request by ensuring that the user is authenticated and authorized
   * to delete the savings goal. It then deletes the savings goal from the repository and returns a
   * {@link GeneralResponseDTO} indicating the status of the request. If the request is invalid, it
   * throws a {@link BadRequestException}. If the user is not authorized to delete the savings goal,
   * it throws a {@link NotAuthorizedException}. If any unexpected error occurs while deleting the
   * savings goal, it throws an {@link InternalServerErrorException}.
   *
   * @param savingsId the id of the savings goal to be deleted
   * @return a {@link GeneralResponseDTO} indicating the status of the request
   * @throws NotFoundException if the savings goal with the given id is not found
   * @throws NotAuthorizedException if the user is not authorized to delete the savings goal
   * @throws InternalServerErrorException if any unexpected error occurs while deleting the savings
   *     goal
   */
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

  /**
   * Determines the status of a savings goal based on its end date, target amount, and saved amount.
   *
   * @param endDate the end date of the savings goal
   * @param targetAmount the target amount of the savings goal
   * @param savedAmount the saved amount of the savings goal
   * @return the status of the savings goal
   */
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

  /**
   * Returns a tooltip string describing the given savings status.
   *
   * @param status the savings status
   * @return a string describing the savings status
   */
  private String getSavingsStatusTooltip(SavingsStatus status) {
    return switch (status) {
      case ACHIEVED -> "You have successfully reached your savings goal!";
      case FAILED -> "The savings goal was not achieved before the end date.";
      case ON_TRACK -> "You are currently on track to reach your savings goal.";
    };
  }

  /**
   * Calculates the total amount saved by a savings goal by summing the amounts of all expenses
   * associated with the savings goal.
   *
   * @param savingsId the ID of the savings goal
   * @return the total amount saved
   */
  private BigDecimal calculateSavedAmount(Long savingsId) {
    return expenseRepository.getTotalExpenseBySavingsId(savingsId);
  }
}
