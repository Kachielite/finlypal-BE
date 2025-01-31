package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.CategoryResponseDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> getAllCategories(int page, int pageSize)
            throws InternalServerErrorException;

    CategoryResponseDTO getCategoryById(Long id)
            throws NotFoundException, InternalServerErrorException;

    List<CategoryResponseDTO> getCategoriesByName(String categoryName, int page, int pageSize)
            throws InternalServerErrorException;

    List<CategoryResponseDTO> getCategoriesByExpenseId(Long expenseId, int page, int pageSize)
            throws InternalServerErrorException, BadRequestException;
}
