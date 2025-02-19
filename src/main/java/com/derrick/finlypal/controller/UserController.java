package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.UsersResponseDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(
    name = "Users",
    description =
        "API endpoints for managing users. These endpoints are used for updating user details, fetching user details and more.")
public class UserController {

  private final UsersService usersService;

  @GetMapping("/{user_id}")
  @Operation(
      summary = "Fetch current logged in user's details",
      description =
          "Fetches the details of the user currently logged in. This endpoint is useful for fetching the user's details, such as their name and email address. The details are returned in the response body as a JSON Object.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User details fetched successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<UsersResponseDTO> getUser(
      @NotEmpty(message = "user_id cannot be empty") @PathVariable String user_id)
      throws NotFoundException,
          InternalServerErrorException,
          BadRequestException,
          NotAuthorizedException {
    return new ResponseEntity<>(usersService.getUserDetails(Long.valueOf(user_id)), HttpStatus.OK);
  }

  @PutMapping("/{user_id}")
  @Operation(
      summary = "Update User Details",
      description =
          "Updates the details of the user currently logged in. This endpoint is used to update the user's name and password. The new details are provided in the request body as a JSON Object. The response body will contain the updated user details as a JSON Object.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User details updated successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ResponseEntity<GeneralResponseDTO> updateUserDetail(
      @Valid @RequestBody UsersUpdateRequestDTO userDetailsDTO, @PathVariable String user_id)
      throws NotFoundException,
          InternalServerErrorException,
          BadRequestException,
          NotAuthorizedException {

    return new ResponseEntity<>(
        usersService.updateUserDetails(Long.valueOf(user_id), userDetailsDTO), HttpStatus.OK);
  }
}
