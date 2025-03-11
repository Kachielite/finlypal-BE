package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.SavingsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsItemRepository extends JpaRepository<SavingsItem, Long> {
    Page<SavingsItem> findAllBySavingsId(Long id, Pageable pageable);
}
