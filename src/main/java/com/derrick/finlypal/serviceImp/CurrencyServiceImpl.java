package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.CurrencyResponseDTO;
import com.derrick.finlypal.entity.Currency;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.repository.CurrencyRepository;
import com.derrick.finlypal.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;

    /**
     * Retrieves all currencies.
     *
     * @return a list of {@link CurrencyResponseDTO}.
     */
    @Override
    public List<CurrencyResponseDTO> findAll() throws InternalServerErrorException {
        log.info("Received request to get all currencies");
        try {
            List<Currency> currencies = currencyRepository.findAll();

            return currencies.stream().map(
                    currency -> CurrencyResponseDTO
                            .builder()
                            .code(currency.getCode())
                            .name(currency.getName())
                            .symbol(currency.getSymbol())
                            .build()
            ).toList();
        } catch (Exception e) {
            log.error("Failed to get currencies", e);
            throw new InternalServerErrorException("Failed to get currencies");
        }
    }
}
