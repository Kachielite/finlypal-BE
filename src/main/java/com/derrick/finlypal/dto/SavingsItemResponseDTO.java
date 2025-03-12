package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.SavingsItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Savings", description = "Holds information about savings item")
public class SavingsItemResponseDTO {
  @Schema(description = "Id of savings item", example = "1")
  private Long id;

  @Schema(description = "Name of savings item", example = "Passport")
  private String name;

  @Schema(description = "Allocated amount of savings item", example = "100.00")
  private BigDecimal allocatedAmount;

  @Schema(description = "Status of savings item", example = "ACTIVE")
  private SavingsItemStatus status;

  @Schema(description = "Id of savings this saving item belongs to", example = "1")
  private Long savingsId;

  @Schema(description = "createdAt of savings item", example = "2023-08-01")
  private LocalDate createdAt;
}
