package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "CategoryResponse", description = "Holds information about a category")
public class CategoryResponseDTO {

  @Schema(description = "Id of category", example = "1")
  private Long id;

  @Schema(description = "Name of category", example = "Groceries")
  private String name;

  @Schema(description = "Display name of category", example = "Groceries")
  @JsonProperty("display_name")
  private String displayName;

  @Schema(description = "Description of category", example = "Groceries")
  private String description;
}
