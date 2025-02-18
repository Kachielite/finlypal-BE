package com.derrick.finlypal.controller;

import com.derrick.finlypal.dto.AuthenticationRequestDTO;
import com.derrick.finlypal.dto.AuthenticationResponseDTO;
import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.UsersRegistrationRequestDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "APIs related to user authentication and registration, including login, logout, and account creation"
)
public class AuthenticationController {

    private final AuthService authService;


    @PostMapping("/login")
    @SecurityRequirements()
    @Operation(
            summary = "User login",
            description = "Authenticates a user with their email and password, and returns an access token for accessing protected APIs. "
                    + "The access token is a JSON Web Token (JWT) that is valid for a limited time period."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )

    })
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO
    ) throws InternalServerErrorException, NotFoundException {

        return new ResponseEntity<>(
                authService.login(authenticationRequestDTO),
                HttpStatus.OK
        );
    }

    @PostMapping("/register")
    @SecurityRequirements()
    @Operation(
            summary = "Register New User",
            description = "Handles the registration of a new user account in the system. The user will need to provide a valid email address and password. " +
                    "The password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, " +
                    "one digit, and one special character. " +
                    "The user will receive an activation email with a link to activate their account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )

    })
    public ResponseEntity<AuthenticationResponseDTO> register(
            @Valid @RequestBody UsersRegistrationRequestDTO usersRegistrationRequestDTO
    ) throws InternalServerErrorException {

        return new ResponseEntity<>(
                authService.register(usersRegistrationRequestDTO),
                HttpStatus.OK
        );
    }

    @PostMapping("/refresh-token")
    @SecurityRequirements()
    @Operation(
            summary = "Refresh Access Token",
            description = """
                    Refresh user's authentication access token. This endpoint is used to
                    refresh the user's access token when the current one has expired.
                    The endpoint requires the user to have a valid refresh token.
                    The refresh token is sent via the Authorization header.
                    The response contains the new access and refresh tokens.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token refreshed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )

    })
    public void refreshToken(HttpServletResponse response, HttpServletRequest request)
            throws NotFoundException, InternalServerErrorException {
        authService.refreshToken(request, response);
    }

    @GetMapping("/reset-password-token")
    @SecurityRequirements()
    @Operation(
            summary = "Generate Reset Password Token",
            description = """
                    Generates a reset password token link that is sent to the provided email.
                    The token is valid for 3 hours and can be used to reset the user's password.
                    The endpoint requires the user to have a valid email address.
                    The response contains a message indicating that the password reset token has been sent.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset token generated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )

    })
    public ResponseEntity<GeneralResponseDTO> resetPasswordToken(
            @Email(message = "Email must be a valid email address")
            @NotEmpty(message = "Email cannot be empty")
            @RequestParam String email
    ) throws InternalServerErrorException, BadRequestException {

        return new ResponseEntity<>(
                new GeneralResponseDTO(HttpStatus.OK, authService.getPasswordRequestToken(email)),
                HttpStatus.OK
        );
    }

    @PostMapping("/reset-password")
    @SecurityRequirements()
    @Operation(
            summary = "Reset User Password",
            description = """
                    Resets a user's password. The user needs to provide a valid reset token and new password.
                    The reset token is valid for 3 hours and can be used only once.
                    The new password must be at least.
                    The user will receive an email notification with the new password once the password has been reset.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Update user information successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )

    })
    public ResponseEntity<GeneralResponseDTO> resetPassword(
            @Valid @RequestBody UsersUpdateRequestDTO updateRequestDTO,
            @NotEmpty(message = "Token cannot be empty")
            @RequestParam String token
    ) throws InternalServerErrorException, BadRequestException, NotFoundException, NotAuthorizedException {

        return new ResponseEntity<>(
                new GeneralResponseDTO(HttpStatus.OK, authService.resetPassword(token, updateRequestDTO)),
                HttpStatus.OK);

    }
}
