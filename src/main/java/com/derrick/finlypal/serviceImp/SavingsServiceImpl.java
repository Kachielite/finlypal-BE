package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.SavingsRequestDTO;
import com.derrick.finlypal.dto.SavingsResponseDTO;
import com.derrick.finlypal.entity.Savings;
import com.derrick.finlypal.enums.SavingsStatus;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.SavingsRepository;
import com.derrick.finlypal.service.SavingsService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsServiceImpl implements SavingsService {
    private final SavingsRepository savingsRepository;

    @Override
    public SavingsResponseDTO createSavings(SavingsRequestDTO savingsRequestDTO) throws BadRequestException, InternalServerErrorException {
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

            Savings savings = Savings.builder()
                    .goalName(savingsRequestDTO.goalName())
                    .targetAmount(savingsRequestDTO.targetAmount())
                    .savedAmount(BigDecimal.ZERO)
                    .startDate(savingsRequestDTO.startDate())
                    .endDate(savingsRequestDTO.endDate())
                    .user(GetLoggedInUserUtil.getUser())
                    .status(getSavingsStatus(savingsRequestDTO.endDate(), savingsRequestDTO.targetAmount(), BigDecimal.ZERO))
                    .build();

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
            log.error("Error creating savings: {}", e.getMessage());
            throw new InternalServerErrorException("Error creating savings : " + e.getMessage());
        }
    }

    @Override
    public SavingsResponseDTO updateSavings(Long savingsId, SavingsRequestDTO savingsRequestDTO) throws BadRequestException, NotFoundException, NotAuthorizedException, InternalServerErrorException {
        return null;
    }

    @Override
    public SavingsResponseDTO getSavingsById(Long savingsId) throws NotFoundException, NotAuthorizedException, InternalServerErrorException {
        return null;
    }

    @Override
    public Page<SavingsResponseDTO> getAllSavings(int page, int pageSize) throws NotAuthorizedException, InternalServerErrorException {
        return null;
    }

    @Override
    public void deleteSavings(Long savingsId) throws NotFoundException, NotAuthorizedException, InternalServerErrorException {

    }

    private SavingsStatus getSavingsStatus(LocalDate endDate, BigDecimal targetAmount, BigDecimal savedAmount) {
        LocalDate today = LocalDate.now();

        if (savedAmount.compareTo(targetAmount) >= 0) {
            return SavingsStatus.ACHIEVED;
        }

        if (endDate.isBefore(today)) {
            return SavingsStatus.FAILED;
        }

        return SavingsStatus.ON_TRACK;
    }
}
