package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByNameContaining(String name, Pageable pageable);

    List<Category> findByExpensesId(Long expensesId, Pageable pageable);

    List<Category> findAllByOrderByNameAsc();

}
