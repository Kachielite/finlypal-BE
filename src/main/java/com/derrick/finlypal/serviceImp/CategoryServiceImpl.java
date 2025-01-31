package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.CategoryResponseDTO;
import com.derrick.finlypal.entity.Category;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.CategoryRepository;
import com.derrick.finlypal.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    List<Category> categoryList = new ArrayList<>();

    @Override
    public List<CategoryResponseDTO> getAllCategories(int page, int pageSize)
            throws InternalServerErrorException {
        log.info("Received request to get all categories");
        try {
            Pageable pageable = (Pageable) PageRequest.of(page, pageSize);
            log.info("Retrieving all categories");
            List<Category> categories = categoryRepository.findAll(pageable);

            categoryList.addAll(categories);
            return convertCategoryListToCategoryResponseDTOList(categories, false);

        } catch (Exception e) {
            log.error("Error occurred while getting categories", e);
            throw new InternalServerErrorException("An error occurred while getting the categories: " + e.getMessage());
        }
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id)
            throws NotFoundException, InternalServerErrorException {
        log.info("Received request to get category for id {} ", id);
        try {
            Category category = categoryRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Category not found with id: " + id)
            );

            log.info("Retrieved category {}", category);
            return CategoryResponseDTO
                    .builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .displayName(category.getDisplayName())
                    .build();

        } catch (NotFoundException e) {
            log.info("Category with id {} not found", id);
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while getting category for id {} ", id, e);
            throw new InternalServerErrorException("An error occurred while getting the category: " + e.getMessage());
        }
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByName(String categoryName, int page, int pageSize)
            throws InternalServerErrorException {
        log.info("Received request to get categories by name {} ", categoryName);
        if (categoryName == null || categoryName.isEmpty()) {
            log.info("Received request to get categories by name empty");
            return new ArrayList<>();
        }

        try {
            Pageable pageable = (Pageable) PageRequest.of(page, pageSize);
            log.info("Retrieving all categories by name {}", categoryName);
            List<Category> categories = categoryRepository.findByNameContaining(categoryName, pageable);

            categoryList.addAll(categories);
            log.info("Retrieved all categories by name {}", categoryName);
            return convertCategoryListToCategoryResponseDTOList(categories, false);

        } catch (Exception e) {
            log.error("Error occurred while getting categories by name {} ", categoryName, e);
            throw new InternalServerErrorException("An error occurred while getting the categories: " + e.getMessage());
        }

    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByExpenseId(Long expenseId, int page, int pageSize)
            throws InternalServerErrorException, BadRequestException {
        log.info("Received request to get categories by expense id {} ", expenseId);
        try {
            if (expenseId == null || expenseId == 0) {
                log.info("Received request to get categories by expense id empty");
                throw new BadRequestException("Expense id is null or empty");
            }

            Pageable pageable = (Pageable) PageRequest.of(page, pageSize);
            log.info("Retrieving all categories by expense id {}", expenseId);
            List<Category> categories = categoryRepository.findByExpensesId(expenseId, pageable);

            categoryList.addAll(categories);
            log.info("Retrieved all categories by expense id {}", expenseId);
            return convertCategoryListToCategoryResponseDTOList(categories, true);

        } catch (BadRequestException e) {
            log.error("Error occurred while getting categories by expense id {}", expenseId, e);
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while getting categories by expense id {}", expenseId, e);
            throw new InternalServerErrorException("An error occurred while getting the categories: " + e.getMessage());
        }

    }

    private List<CategoryResponseDTO> convertCategoryListToCategoryResponseDTOList(
            List<Category> categories,
            boolean includeExpenses) {

        List<CategoryResponseDTO> categoryResponseDTOList = new ArrayList<>();

        for (Category category : categories) {
            CategoryResponseDTO categoryResponseDTO = CategoryResponseDTO
                    .builder()
                    .id(includeExpenses ? null : category.getId())
                    .name(includeExpenses ? null : category.getName())
                    .description(includeExpenses ? null : category.getDescription())
                    .displayName(includeExpenses ? null : category.getDisplayName())
                    .expenses(includeExpenses ? category.getExpenses() : null)
                    .build();

            categoryResponseDTOList.add(categoryResponseDTO);
        }

        return categoryResponseDTOList;
    }
}
