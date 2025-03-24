package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
