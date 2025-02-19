package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.CategoryResponseDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@Tag(
    name = "Category",
    description =
        """
                Manage expense categories.
                This endpoint provides functionalities to interact with expense categories.
                You can use the endpoints to list and search categories.
                """)
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  @Operation(
      summary = "List Categories",
      description =
          """
                    Get the list of all categories.

                    Query parameters:
                    - page: the page number of the result (default: 0)
                    - pageSize: the number of items per page (default: 10)
                    """)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<Page<CategoryResponseDTO>> getCategories(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize)
      throws InternalServerErrorException {
    return new ResponseEntity<>(categoryService.getAllCategories(page, pageSize), HttpStatus.OK);
  }

  @GetMapping("/{category_id}")
  @Operation(
      summary = "Retrieve Category by ID",
      description =
          "Fetch the details of a specific category using its unique identifier (category_id). This operation returns the category's name, description, and display name if found.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Category fetched successfully"),
    @ApiResponse(
        responseCode = "404",
        description = "Category not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long category_id)
      throws NotFoundException, InternalServerErrorException {
    return new ResponseEntity<>(categoryService.getCategoryById(category_id), HttpStatus.OK);
  }

  @GetMapping("/search")
  @Operation(
      summary = "Search Category",
      description =
          """
                    Search category by name.

                    Query parameters:
                    - name: the name of the category to search for
                    - page: the page number of the result (default: 0)
                    - pageSize: the number of items per page (default: 10)
                    """)
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<Page<CategoryResponseDTO>> searchCategoryByName(
      @RequestParam String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize)
      throws InternalServerErrorException {
    return new ResponseEntity<>(
        categoryService.getCategoriesByName(name, page, pageSize), HttpStatus.OK);
  }
}
