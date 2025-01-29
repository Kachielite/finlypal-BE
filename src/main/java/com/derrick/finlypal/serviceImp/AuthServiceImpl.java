package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.AuthenticationRequestDTO;
import com.derrick.finlypal.dto.AuthenticationResponseDTO;
import com.derrick.finlypal.dto.UsersRegistrationRequestDTO;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.exception.UserAlreadyExistsException;
import com.derrick.finlypal.repository.UserRepository;
import com.derrick.finlypal.service.AuthService;
import com.derrick.finlypal.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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
            throw new NotFoundException("User with email " + authenticationRequestDTO.email() + " not found");
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user with email {}", authenticationRequestDTO.email());
            throw new BadCredentialsException("Invalid email or password");

        } catch (Exception e) {
            log.error("Error authenticating user with email {}", authenticationRequestDTO.email());
            throw new InternalServerErrorException("An unknown error occurred. User could not be authenticated.");
        }
    }

    @Override
    public AuthenticationResponseDTO register(UsersRegistrationRequestDTO usersRegistrationRequestDTO)
            throws InternalServerErrorException, UserAlreadyExistsException {
        try {
            log.info("Registration request received for the user {}", usersRegistrationRequestDTO.toString());

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
            throw new UserAlreadyExistsException(usersRegistrationRequestDTO.email());
        } catch (Exception e) {
            log.error("Error while registering user {}", usersRegistrationRequestDTO.toString());
            throw new InternalServerErrorException("An unknown error occurred. User could not be registered.");
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
                new ObjectMapper().writeValue(response.getOutputStream(), responseDTO);
            }

        } catch (NotFoundException e) {
            log.error("Could not find user with email {}", username);
            throw new NotFoundException("User with email " + username + " not found");
        } catch (RuntimeException | IOException e) {
            throw new InternalServerErrorException("An unknown error occurred. User could not be authenticated.");
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
