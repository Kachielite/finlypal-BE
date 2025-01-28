package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.ApiResponseDTO;
import com.derrick.finlypal.dto.AuthenticationRequestDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.UsersRegistrationRequestDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.exception.UserAlreadyExistsException;
import com.derrick.finlypal.service.AuthService;
import com.derrick.finlypal.serviceImp.InputValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;
    private final InputValidation inputValidation;

    @PostMapping("/login")
    ResponseEntity<ApiResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO,
            BindingResult bindingResult) {

        try {
            ResponseEntity<ErrorResponseDTO> errors = inputValidation.validate(bindingResult);
            if (errors != null) return new ResponseEntity<>(errors.getBody(), errors.getStatusCode());

            return new ResponseEntity<>(authService.login(authenticationRequestDTO), HttpStatus.CREATED);

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .build(), HttpStatus.UNAUTHORIZED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .build(), HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponseDTO> register(
            @Valid @RequestBody UsersRegistrationRequestDTO usersRegistrationRequestDTO,
            BindingResult bindingResult) {

        try {
            ResponseEntity<ErrorResponseDTO> errors = inputValidation.validate(bindingResult);
            if (errors != null) return new ResponseEntity<>(errors.getBody(), errors.getStatusCode());

            return new ResponseEntity<>(authService.register(usersRegistrationRequestDTO), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.CONFLICT
            );
        }
    }
}
