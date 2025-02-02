package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByDisplayNameContaining(String name, Pageable pageable);

    Page<Category> findAll(Pageable pageable);

}
