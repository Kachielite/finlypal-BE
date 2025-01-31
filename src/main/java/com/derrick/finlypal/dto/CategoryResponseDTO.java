package com.derrick.finlypal.dto;

import com.derrick.finlypal.entity.Expense;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private List<Expense> expenses;
}
