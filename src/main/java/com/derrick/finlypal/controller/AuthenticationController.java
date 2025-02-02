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

import java.util.Map;

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
    public ResponseEntity<ApiResponseDTO<?>> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO,
            BindingResult bindingResult) {

        try {
            // Validate Request Body
            Map<String, String> errors = inputValidation.validate(bindingResult);
            if (errors != null && !errors.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponseDTO<>(400, "Validation Error", errors),
                        HttpStatus.BAD_REQUEST
                );
            }

            return new ResponseEntity<>(
                    new ApiResponseDTO<>(
                            200,
                            "Login successful",
                            authService.login(authenticationRequestDTO)
                    ),
                    HttpStatus.OK
            );

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, e.getMessage(), null),
                    HttpStatus.UNAUTHORIZED
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(404, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user.")
    @SecurityRequirements() // ❌ Exclude from security
    public ResponseEntity<ApiResponseDTO<?>> register(
            @Valid @RequestBody UsersRegistrationRequestDTO usersRegistrationRequestDTO,
            BindingResult bindingResult) {

        try {
            // Validate Request Body
            Map<String, String> errors = inputValidation.validate(bindingResult);
            if (errors != null && !errors.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponseDTO<>(400, "Validation Error", errors),
                        HttpStatus.BAD_REQUEST
                );
            }

            AuthenticationResponseDTO response = authService.register(usersRegistrationRequestDTO);

            return new ResponseEntity<>(
                    new ApiResponseDTO<>(201, "Account created successfully", response),
                    HttpStatus.OK
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(409, e.getMessage(), null),
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
    public ResponseEntity<?> resetPasswordToken(@RequestParam String email) {
        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(200, "Password Token sent", authService.getPasswordRequestToken(email)),
                    HttpStatus.OK
            );

        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset User Password", description = "Reset a user's password")
    @SecurityRequirements() // ❌ Exclude from security
    public ResponseEntity<ApiResponseDTO<?>> resetPassword(
            @Valid @RequestBody UsersUpdateRequestDTO updateRequestDTO,
            @RequestParam String token,
            BindingResult bindingResult
    ) {
        // Validate Request Body
        Map<String, String> errors = inputValidation.validate(bindingResult);
        if (errors != null && !errors.isEmpty()) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, "Validation Error", errors),
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(200, "Password Token sent", authService.resetPassword(token, updateRequestDTO)),
                    HttpStatus.OK);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(500, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (BadRequestException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(400, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(401, "Unauthorized", null),
                    HttpStatus.FORBIDDEN
            );
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    new ApiResponseDTO<>(404, e.getMessage(), null),
                    HttpStatus.NOT_FOUND
            );
        }

    }
}
