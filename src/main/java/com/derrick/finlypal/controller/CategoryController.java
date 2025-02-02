package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category", description = "Manage expense categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List Categories", description = "Get the list of all categories")
    public ResponseEntity<ApiResponseDTO> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            return new ResponseEntity<>(categoryService.getAllCategories(page, pageSize), HttpStatus.OK);

        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
