package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "ErrorResponse", description = "Holds error details")
public class ErrorResponseDTO {

  @JsonProperty("api_path")
  @Schema(description = "API path", example = "/api/v1/transactions")
  private String apiPath;

  @Schema(description = "Error code")
  private HttpStatus code;

  @Schema(description = "Error message")
  private String message;

  @Schema(description = "Timestamp", example = "2023-08-01T12:34:56")
  private LocalDateTime timestamp;
}
