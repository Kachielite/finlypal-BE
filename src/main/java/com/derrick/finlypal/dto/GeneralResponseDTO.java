package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "GeneralResponse", description = "Holds the general response")
public class GeneralResponseDTO {
    @Schema(description = "Status code", example = "200")
    private HttpStatus status;

    @Schema(description = "Message", example = "Successful Operation")
    private String message;

}
