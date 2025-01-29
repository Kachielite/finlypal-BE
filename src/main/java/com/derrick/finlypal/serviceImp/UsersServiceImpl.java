package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.UsersResponseDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.UserRepository;
import com.derrick.finlypal.service.UsersService;
import com.derrick.finlypal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsersResponseDTO getUserDetails(Long userId)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException {

        try {
            log.info("Received request to get user details for {}", userId);
            User user = validateUserAccess(userId);

            log.info("User details found for user {}", userId);
            return UsersResponseDTO
                    .builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();

        } catch (NotFoundException e) {
            log.error("User not found for user {}", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        } catch (NotAuthorizedException e) {
            log.error("User not authorized for user {}", userId);
            throw new NotAuthorizedException("Access denied: User with id " + userId + " not authorized to access this resource");
        } catch (Exception e) {
            log.error("Error while getting user details", e);
            throw new InternalServerErrorException("An internal server error occurred");
        }
    }

    @Override
    public GeneralResponseDTO updateUserDetails(Long userId, UsersUpdateRequestDTO updateRequestDTO)
            throws NotFoundException, InternalServerErrorException, NotAuthorizedException, BadRequestException {
        try {
            log.info("Received request to update user details for {}", userId);
            User user = validateUserAccess(userId);

            if (updateRequestDTO.name() != null) {
                user.setName(updateRequestDTO.name());
            }

            if (updateRequestDTO.oldPassword() != null || updateRequestDTO.newPassword() != null) {
                if (!passwordEncoder.matches(updateRequestDTO.oldPassword(), user.getPassword())) {
                    throw new BadRequestException("Old password does not match");
                }

                user.setPassword(passwordEncoder.encode(updateRequestDTO.newPassword()));
            }

            return GeneralResponseDTO
                    .builder()
                    .status(HttpStatus.OK)
                    .message("User details updated successfully")
                    .build();

        } catch (NotFoundException e) {
            log.error("User not found for user {}", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        } catch (NotAuthorizedException e) {
            log.error("User not authorized for user {}", userId);
            throw new NotAuthorizedException("Access denied: User with id " + userId + " not authorized to access this resource");
        } catch (BadRequestException e) {
            log.error("Password updated failed {}", userId);
            throw new NotAuthorizedException("Old password does not match");
        } catch (Exception e) {
            log.error("Error while getting user details", e);
            throw new InternalServerErrorException("An internal server error occurred");
        }
    }

    private User validateUserAccess(Long userId) throws NotAuthorizedException, NotFoundException {
        log.info("Getting user details for {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        log.info("Validating access for user {}", userId);
        jwtUtil.validateUserAccess(user.getUsername());

        return user;
    }
}
