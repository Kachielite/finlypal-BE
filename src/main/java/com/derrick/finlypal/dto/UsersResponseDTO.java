package com.derrick.finlypal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "UsersResponse", description = "Holds user details")
public class UsersResponseDTO {

    @Schema(name = "id", description = "User id", example = "1")
    private Long id;

    @Schema(name = "name", description = "User name", example = "John Doe")
    private String name;

    @Schema(name = "email", description = "User email", example = "XK0eI@example.com")
    private String email;

}
