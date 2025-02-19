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
@Schema(
    name = "AuthenticationResponse",
    description = "Holds the authentication response which includes access token and refresh token")
public class AuthenticationResponseDTO {

  @Schema(description = "Access token", example = "eJDDJDSMDLSDOINDSJNDAODNSODINASDKASDSAD")
  @JsonProperty("access_token")
  private String accessToken;

  @Schema(description = "Refresh token", example = "eJDDJDSMDLSDOINDSJNDAODNSODINASDKASDSAD")
  @JsonProperty("refresh_token")
  private String refreshToken;
}
