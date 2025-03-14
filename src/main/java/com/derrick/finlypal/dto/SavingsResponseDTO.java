package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.SavingsStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SavingsResponse", description = "Holds information about savings")
public class SavingsResponseDTO {

  @Schema(description = "Id of savings", example = "1")
  private Long id;

  @Schema(description = "Name of savings", example = "Travel")
  @JsonProperty("goal_name")
  private String goalName;

  @Schema(description = "Target amount of savings", example = "100.00")
  @JsonProperty("target_amount")
  private BigDecimal targetAmount;

  @Schema(description = "Saved amount of savings", example = "50.00")
  @JsonProperty("saved_amount")
  private BigDecimal savedAmount;

  @Schema(description = "Start date of savings", example = "2023-08-01")
  @JsonProperty("start_date")
  private String startDate;

  @Schema(description = "End date of savings", example = "2023-08-31")
  @JsonProperty("end_date")
  private String endDate;

  @Schema(description = "Status of savings", example = "ACTIVE")
  private SavingsStatus status;

  @Schema(description = "List of expenses associated with savings", example = "[]")
  private List<ExpenseResponseDTO> expenses;

  @Schema(description = "savings creation date", example = "2023-08-01")
  @JsonProperty("created_at")
  private LocalDate createdAt;
}
