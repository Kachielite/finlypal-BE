package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record CategoryRequestDTO(
        String name,
        @Min(value = 1, message = "Page value can not be less than 1")
        int page,
        @Min(value = 10)
        @JsonProperty("page_size")
        int pageSize,
        @JsonProperty("start_date")
        LocalDate startDate,
        @JsonProperty("end_date")
        @Future(message = "End date must be greater than start date")
        LocalDate endDate
) {
}
