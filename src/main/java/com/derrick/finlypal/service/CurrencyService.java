package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.CurrencyResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;

import java.util.List;

public interface CurrencyService {
    List<CurrencyResponseDTO> findAll() throws InternalServerErrorException;
}
