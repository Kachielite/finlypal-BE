package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.*;
import com.derrick.finlypal.exception.*;
import com.derrick.finlypal.service.AuthService;
import com.derrick.finlypal.util.InputValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthenticationController {

    private final AuthService authService;
    private final InputValidation inputValidation;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns an access token.")
    @SecurityRequirements() // ❌ Exclude from security
    public ResponseEntity<ApiResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO,
            BindingResult bindingResult) {

        try {
            ResponseEntity<ErrorResponseDTO> errors = inputValidation.validate(bindingResult);
            if (errors != null) return new ResponseEntity<>(errors.getBody(), errors.getStatusCode());

            return new ResponseEntity<>(authService.login(authenticationRequestDTO), HttpStatus.CREATED);

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message(e.getMessage())
                    .build(), HttpStatus.UNAUTHORIZED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .code(HttpStatus.NOT_FOUND.value())
                    .build(), HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .message(e.getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user.")
    @SecurityRequirements() // ❌ Exclude from security
    public ResponseEntity<ApiResponseDTO> register(
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
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.CONFLICT.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.CONFLICT
            );
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Access Token", description = "Refresh user's authentication access token")
    @SecurityRequirements() // ❌ Exclude from security
    public void refreshToken(HttpServletResponse response, HttpServletRequest request)
            throws NotFoundException, InternalServerErrorException {
        authService.refreshToken(request, response);
    }

    @GetMapping("/reset-password-token")
    @Operation(summary = "Generate Password Reset Token", description = "Generates a reset password token link that is sent to the provided email")
    @SecurityRequirements() // ❌ Exclude from security
    public ResponseEntity<ApiResponseDTO> resetPasswordToken(@RequestParam String email) {
        try {
            return new ResponseEntity<>(authService.getPasswordRequestToken(email), HttpStatus.OK);

        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
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

    @PostMapping("/reset-password")
    @Operation(summary = "Reset User Password", description = "Reset a user's password")
    @SecurityRequirements() // ❌ Exclude from security
    public ResponseEntity<ApiResponseDTO> resetPassword(
            @Valid @RequestBody UsersUpdateRequestDTO updateRequestDTO,
            @RequestParam String token,
            BindingResult bindingResult
    ) {
        ResponseEntity<ErrorResponseDTO> errors = inputValidation.validate(bindingResult);
        if (errors != null) return new ResponseEntity<>(errors.getBody(), errors.getStatusCode());

        try {
            return new ResponseEntity<>(authService.resetPassword(token, updateRequestDTO), HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.BAD_REQUEST
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
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    ErrorResponseDTO
                            .builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

    }
}
