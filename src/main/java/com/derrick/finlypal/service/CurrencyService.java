package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.CurrencyResponseDTO;

import java.util.List;

public interface CurrencyService {
    List<CurrencyResponseDTO> findAll();
}
