package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category", description = "Manage expense categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List Categories", description = "Get the list of all categories")
    public ResponseEntity<ApiResponseDTO<?>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            200,
                            "Successfully fetched category list",
                            categoryService.getAllCategories(page, pageSize)
                    ),
                    HttpStatus.OK
            );

        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{category_id}")
    @Operation(summary = "Fetch category by id", description = "Find a category using category_id")
    public ResponseEntity<ApiResponseDTO<?>> getCategoryById(
            @PathVariable Long category_id
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            200,
                            "Successfully fetched category with id:" + category_id,
                            categoryService.getCategoryById(category_id)
                    ),
                    HttpStatus.OK
            );

        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(404, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search Category", description = "Search category by name")
    public ResponseEntity<ApiResponseDTO<?>> searchCategoryByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            200,
                            "Successfully fetched categories matching name:" + name,
                            categoryService.getCategoriesByName(name, page, pageSize)
                    ),
                    HttpStatus.OK
            );

        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


}
