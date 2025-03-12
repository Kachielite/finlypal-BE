package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.SavingsRequestDTO;
import com.derrick.finlypal.dto.SavingsResponseDTO;
import org.springframework.data.domain.Page;

public interface SavingsService {

  SavingsResponseDTO createSavings(SavingsRequestDTO savingsRequestDTO);

  SavingsResponseDTO updateSavings(Long savingsId, SavingsRequestDTO savingsRequestDTO);

  SavingsResponseDTO getSavingsById(Long savingsId);

  Page<SavingsResponseDTO> getAllSavings(int page, int pageSize);

  void deleteSavings(Long savingsId);
}
