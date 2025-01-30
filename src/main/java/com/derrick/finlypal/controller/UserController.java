package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.UsersService;
import com.derrick.finlypal.util.InputValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;
    private final InputValidation inputValidation;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDTO> getUser(@PathVariable String userId) {
        try {
            return new ResponseEntity<>(usersService.getUserDetails(Long.valueOf(userId)), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.FORBIDDEN.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.FORBIDDEN
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponseDTO> updateUserDetail(
            @Valid @RequestBody UsersUpdateRequestDTO userDetailsDTO,
            BindingResult bindingResult,
            @PathVariable String userId) {

        ResponseEntity<ErrorResponseDTO> errors = inputValidation.validate(bindingResult);
        if (errors != null) return new ResponseEntity<>(errors.getBody(), errors.getStatusCode());

        try {
            return new ResponseEntity<>(usersService.updateUserDetails(Long.valueOf(userId), userDetailsDTO), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.FORBIDDEN.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.FORBIDDEN
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
