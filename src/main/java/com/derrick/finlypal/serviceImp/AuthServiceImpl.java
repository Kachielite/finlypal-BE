package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.*;
import com.derrick.finlypal.entity.ResetToken;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.exception.*;
import com.derrick.finlypal.repository.ResetTokenRepository;
import com.derrick.finlypal.repository.UserRepository;
import com.derrick.finlypal.service.AuthService;
import com.derrick.finlypal.service.EmailService;
import com.derrick.finlypal.util.JwtUtil;
import com.derrick.finlypal.util.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final ResetTokenRepository resetTokenRepository;

    @Override
    public AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO)
            throws InternalServerErrorException, NotFoundException, BadCredentialsException {
        try {
            log.info("Received login request for user with email {}", authenticationRequestDTO.email());

            User user = userRepository.findByEmail(
                            authenticationRequestDTO.email())
                    .orElseThrow(() -> new NotFoundException(
                            "User with email " + authenticationRequestDTO.email() + " not found"
                    ));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequestDTO.email(), authenticationRequestDTO.password())
            );

            log.info("Authentication successful for user with email {}", authenticationRequestDTO.email());
            return generateAuthResponse(user);

        } catch (NotFoundException e) {
            log.error("Could not find user with email {}", authenticationRequestDTO.email());
            throw e;
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user with email {}", authenticationRequestDTO.email());
            throw e;

        } catch (Exception e) {
            log.error("Error authenticating user with email {}", authenticationRequestDTO.email());
            throw new InternalServerErrorException("An unknown error occurred. User could not be authenticated: " + e.getMessage());
        }
    }

    @Override
    public AuthenticationResponseDTO register(UsersRegistrationRequestDTO usersRegistrationRequestDTO)
            throws InternalServerErrorException, UserAlreadyExistsException {
        try {
            log.info("Registration request received for the user {}", usersRegistrationRequestDTO.toString());

            // Check if user exists
            Optional<User> user = userRepository.findByEmail(usersRegistrationRequestDTO.email());
            if (user.isPresent()) {
                throw new UserAlreadyExistsException("User already exist with email " + usersRegistrationRequestDTO.email());
            }

            log.info("Creating user");
            User newUser = User
                    .builder()
                    .email(usersRegistrationRequestDTO.email())
                    .password(passwordEncoder.encode(usersRegistrationRequestDTO.password()))
                    .name(usersRegistrationRequestDTO.name())
                    .build();

            log.info("Saving user {}", newUser.toString());
            userRepository.save(newUser);

            return generateAuthResponse(newUser);

        } catch (UserAlreadyExistsException e) {
            log.error("User already exist with email {}", usersRegistrationRequestDTO.email());
            throw e;
        } catch (Exception e) {
            log.error("Error while registering user {}", usersRegistrationRequestDTO.toString());
            throw new InternalServerErrorException("An unknown error occurred. User could not be registered: " + e.getMessage());
        }
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws NotFoundException, InternalServerErrorException {
        log.info("Received refresh token request");
        String authHeader = request.getHeader("Authorization");
        String username = null;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token found");
            return;
        }

        try {
            log.info("Extracting username from auth header");
            String refreshToken = authHeader.substring(7);
            username = jwtUtil.extractUsername(refreshToken);

            if (username != null) {
                String finalUsername = username;

                log.info("Validating username {}", username);
                User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException(
                        "User with email " + finalUsername + " not found")
                );

                log.info("Generating new token for user {}", finalUsername);
                String accessToken = jwtUtil.generateAccessToken(user);

                AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                log.info("Successfully refreshed token");
                new ObjectMapper().writeValue(response.getOutputStream(), new ApiResponseDTO<>(200, "Successfully refreshed token", responseDTO));
            }

        } catch (NotFoundException e) {
            log.error("Could not find user with email", e);
            throw e;
        } catch (RuntimeException | IOException e) {
            log.error("Error while refreshing token", e);
            throw new InternalServerErrorException("An unknown error occurred: " + e.getMessage());
        }


    }

    @Override
    public GeneralResponseDTO getPasswordRequestToken(String email)
            throws InternalServerErrorException, BadRequestException {

        try {

            if (email == null || !PATTERN.matcher(email).matches()) {
                throw new BadRequestException("Email not provided or is not a valid");
            }

            log.info("Generating password request token for user with email {}", email);
            String resetToken = TokenGenerator.generateToken();

            log.info("Saving reset token for user with email {}", email);
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(3);
            ResetToken token = ResetToken
                    .builder()
                    .token(resetToken)
                    .email(email)
                    .expiryDate(expiryDate)
                    .build();
            log.info("Reset token saved for user with email {}", email);
            resetTokenRepository.save(token);

            String url = "https://finlypal.com?resetToken=" + resetToken;
            String subject = "Password Reset";
            String body = "To reset your password click this link: " + url;

            log.info("Sending password request token for user with email {}", email);
            emailService.sendEmail(email, subject, body);

            return GeneralResponseDTO
                    .builder()
                    .message("Password request token generated")
                    .status(HttpStatus.OK)
                    .build();

        } catch (BadRequestException e) {
            log.error("Email not provided or is not a valid", e);
            throw e;
        } catch (Exception e) {
            log.error("Error while generating password request token", e);
            throw new InternalServerErrorException("An unknown error occurred: " + e.getMessage());
        }
    }

    @Override
    public GeneralResponseDTO resetPassword(String resetToken, UsersUpdateRequestDTO updateRequestDTO)
            throws InternalServerErrorException, BadRequestException, NotAuthorizedException, NotFoundException {
        try {
            log.info("Received reset password request");
            ResetToken token = resetTokenRepository.findByToken(resetToken);

            if (resetToken == null || token == null) {
                throw new BadRequestException("Reset token not found");
            }

            if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                log.info("Deleting expired reset token for user with email {}", token.getEmail());
                resetTokenRepository.delete(token);
                throw new NotAuthorizedException("Token has expired");
            }

            User user = userRepository.findByEmail(token.getEmail()).orElseThrow(() ->
                    new NotFoundException("Email associated with token not found")
            );

            log.info("Deleting reset token for user with email {}", token.getEmail());
            resetTokenRepository.delete(token);

            log.info("Updating password for user with email {}", user.getEmail());

            if (updateRequestDTO.newPassword() == null || updateRequestDTO.newPassword().trim().isEmpty()) {
                throw new BadRequestException("New password cannot be null or empty");
            }

            user.setPassword(passwordEncoder.encode(updateRequestDTO.newPassword()));
            userRepository.save(user);

            return GeneralResponseDTO
                    .builder()
                    .message("Password reset successful")
                    .status(HttpStatus.OK)
                    .build();


        } catch (NotFoundException e) {
            log.error("Email associated with token not found", e);
            throw e;
        } catch (BadRequestException e) {
            log.error("Bad request error occurred", e);
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("Token has expired", e);
            throw e;
        } catch (Exception e) {
            log.error("Error while resetting password request token", e);
            throw new InternalServerErrorException("An unknown error occurred: " + e.getMessage());
        }
    }

    private AuthenticationResponseDTO generateAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return AuthenticationResponseDTO
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
