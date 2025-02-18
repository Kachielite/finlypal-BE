package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.CategoryResponseDTO;
import com.derrick.finlypal.entity.Category;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.CategoryRepository;
import com.derrick.finlypal.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Gets all categories.
     *
     * @param page     the page number to retrieve. Starts from 0.
     * @param pageSize the number of items per page.
     * @return a page of {@link CategoryResponseDTO}
     * @throws InternalServerErrorException if an error occurs while retrieving the categories.
     */
    @Override
    public Page<CategoryResponseDTO> getAllCategories(int page, int pageSize)
            throws InternalServerErrorException {
        log.info("Received request to get all categories");
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            log.info("Retrieving all categories");

            Page<Category> categories = categoryRepository.findAll(pageable);
            return convertCategoryListToCategoryResponseDTOList(categories);

        } catch (Exception e) {
            log.error("Error occurred while getting categories", e);
            throw new InternalServerErrorException("An error occurred while getting the categories: " + e.getMessage());
        }
    }

    /**
     * Retrieves a category by id.
     *
     * @param id the id of the category to retrieve
     * @return the category
     * @throws NotFoundException            if the category is not found
     * @throws InternalServerErrorException if an error occurs while retrieving the category
     */
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

    /**
     * Retrieves a page of categories by name.
     *
     * @param categoryName the name of the category to search for
     * @param page         the page number of the result (default: 0)
     * @param pageSize     the number of items per page (default: 10)
     * @return a page of categories
     * @throws InternalServerErrorException if an error occurs while retrieving the categories
     */
    @Override
    public Page<CategoryResponseDTO> getCategoriesByName(String categoryName, int page, int pageSize)
            throws InternalServerErrorException {
        log.info("Received request to get categories by name {} ", categoryName);


        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            log.info("Retrieving all categories by name {}", categoryName);
            Page<Category> categories = categoryRepository.findByDisplayNameContaining(categoryName, pageable);

            log.info("Retrieved all categories by name {}", categoryName);
            return convertCategoryListToCategoryResponseDTOList(categories);

        } catch (Exception e) {
            log.error("Error occurred while getting categories by name {} ", categoryName, e);
            throw new InternalServerErrorException("An error occurred while getting the categories: " + e.getMessage());
        }

    }

    /**
     * Converts a page of categories to a page of category response DTOs.
     *
     * @param categories the page of categories to convert
     * @return a page of category response DTOs
     */
    private Page<CategoryResponseDTO> convertCategoryListToCategoryResponseDTOList(
            Page<Category> categories) {

        // Convert Page<Category> to Page<CategoryResponseDTO>
        return categories.map(category -> CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayName(category.getDisplayName())
                .build());
    }
}
