package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.SavingsRequestDTO;
import com.derrick.finlypal.dto.SavingsResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

public interface SavingsService {

  SavingsResponseDTO createSavings(SavingsRequestDTO savingsRequestDTO)
      throws BadRequestException, InternalServerErrorException;

  SavingsResponseDTO updateSavings(Long savingsId, SavingsRequestDTO savingsRequestDTO)
      throws BadRequestException,
          NotFoundException,
          NotAuthorizedException,
          InternalServerErrorException;

  SavingsResponseDTO getSavingsById(Long savingsId)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException;

  Page<SavingsResponseDTO> getAllSavings(int page, int pageSize)
      throws InternalServerErrorException;

  GeneralResponseDTO deleteSavings(Long savingsId)
      throws NotFoundException, NotAuthorizedException, InternalServerErrorException;
}
