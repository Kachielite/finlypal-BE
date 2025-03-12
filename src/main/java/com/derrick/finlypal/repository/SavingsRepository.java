package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Savings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsRepository extends JpaRepository<Savings, Long> {
  Page<Savings> findAllByUserId(Long id, Pageable pageable);
}
