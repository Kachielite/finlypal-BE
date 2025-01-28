package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.AuthenticationRequestDTO;
import com.derrick.finlypal.dto.AuthenticationResponseDTO;
import com.derrick.finlypal.dto.UsersRegistrationRequestDTO;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.exception.UserAlreadyExistsException;
import com.derrick.finlypal.repository.UserRepository;
import com.derrick.finlypal.service.UsersService;
import com.derrick.finlypal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO)
            throws InternalServerErrorException, NotFoundException {
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
