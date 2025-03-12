package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.SavingsItemRequestDTO;
import com.derrick.finlypal.dto.SavingsItemResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;

public interface SavingsItemService {
  List<SavingsItemResponseDTO> createSavingsItems(
      List<SavingsItemRequestDTO> savingsItems, Long savingsId);

  Page<SavingsItemResponseDTO> getSavingsItems(Long savingsId);

  SavingsItemResponseDTO getSavingsItemById(Long savingsItemId);

  SavingsItemResponseDTO updateSavingsItem(
      Long savingsItemId, SavingsItemRequestDTO savingsItemRequestDTO);

  void deleteSavingsItem(Long savingsItemId);
}
