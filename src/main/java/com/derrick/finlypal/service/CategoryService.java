package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.CategoryResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import org.springframework.data.domain.Page;

public interface CategoryService {

  Page<CategoryResponseDTO> getAllCategories(int page, int pageSize)
      throws InternalServerErrorException;

  CategoryResponseDTO getCategoryById(Long id)
      throws NotFoundException, InternalServerErrorException;

  Page<CategoryResponseDTO> getCategoriesByName(String categoryName, int page, int pageSize)
      throws InternalServerErrorException;
}
