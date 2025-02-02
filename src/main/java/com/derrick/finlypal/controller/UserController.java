package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.UsersService;
import com.derrick.finlypal.util.InputValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Manage user")
public class UserController {

    private final UsersService usersService;
    private final InputValidation inputValidation;

    @GetMapping("/{user_id}")
    @Operation(summary = "Get User Details", description = "Fetch current logged in user's details")
    public ResponseEntity<ApiResponseDTO<?>> getUser(@PathVariable String user_id) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            200,
                            "Fetched user details successfully",
                            usersService.getUserDetails(Long.valueOf(user_id))
                    ),
                    HttpStatus.OK
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(404, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(401, e.getMessage(), null),
                    HttpStatus.FORBIDDEN
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{user_id}")
    @Operation(summary = "Update User Details", description = "Update current logged in user's details")
    public ResponseEntity<ApiResponseDTO<?>> updateUserDetail(
            @Valid @RequestBody UsersUpdateRequestDTO userDetailsDTO,
            BindingResult bindingResult,
            @PathVariable String user_id) {

        // Validate Request Body
        Map<String, String> errors = inputValidation.validate(bindingResult);
        if (errors != null && !errors.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, "Validation failed", errors),
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            200,
                            "Update user details successful",
                            usersService.updateUserDetails(Long.valueOf(user_id), userDetailsDTO)
                    ),
                    HttpStatus.OK
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(404, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(401, e.getMessage(), null),
                    HttpStatus.FORBIDDEN
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
