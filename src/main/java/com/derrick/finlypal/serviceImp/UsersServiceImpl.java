package com.derrick.finlypal.serviceImp;

import com.derrick.finlypal.dto.CurrencyResponseDTO;
import com.derrick.finlypal.dto.GeneralResponseDTO;
import com.derrick.finlypal.dto.UsersResponseDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.entity.Currency;
import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.repository.CurrencyRepository;
import com.derrick.finlypal.repository.UserRepository;
import com.derrick.finlypal.service.UsersService;
import com.derrick.finlypal.util.GetLoggedInUserUtil;
import com.derrick.finlypal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyRepository currencyRepository;

    /**
     * Retrieves the user details associated with the given {@code userId}.
     *
     * <p>This method performs the following steps: - Validates the {@code userId} and checks if the
     * user is authorized. - Retrieves the user details associated with the given {@code userId}. -
     * Returns a {@link UsersResponseDTO} containing the user details.
     *
     * <p>If the {@code userId} is null, it throws a {@link BadRequestException}. If the user is not
     * found with the given {@code userId}, it throws a {@link NotFoundException}. If the user is not
     * authorized to access the user details, it throws a {@link NotAuthorizedException}. If any other
     * unexpected error occurs, it throws an {@link InternalServerErrorException}.
     *
     * @param userId the id of the user to retrieve details for
     * @return a {@link UsersResponseDTO} containing the user details
     * @throws InternalServerErrorException if any unexpected error occurs
     * @throws BadRequestException          if the request is invalid
     * @throws NotFoundException            if the user is not found
     * @throws NotAuthorizedException       if the user is not authorized
     */
    @Override
    public UsersResponseDTO getUserDetails()
            throws NotFoundException,
            InternalServerErrorException,
            NotAuthorizedException,
            BadRequestException {

        try {
            Long loggedInUserId = Objects.requireNonNull(GetLoggedInUserUtil.getUser()).getId();

            if (loggedInUserId == null) {
                throw new BadRequestException("userId is null");
            }

            log.info("Received request to get user details for {}", loggedInUserId);
            User user = validateUserAccess(loggedInUserId);

            log.info("User details found for user {}", loggedInUserId);

            Currency currency = user.getCurrency();
            CurrencyResponseDTO currencyResponseDTO = CurrencyResponseDTO
                    .builder()
                    .id(currency.getId())
                    .symbol(currency.getSymbol())
                    .name(currency.getName())
                    .code(currency.getCode())
                    .build();

            return UsersResponseDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .currency(currencyResponseDTO)
                    .build();

        } catch (BadRequestException e) {
            log.error("userId is null", e);
            throw e;
        } catch (NotFoundException e) {
            log.error("User not found for user", e);
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("User not authorized for user", e);
            throw e;
        } catch (Exception e) {
            log.error("Error while getting user details", e);
            throw new InternalServerErrorException(
                    "An internal server error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates the user details for a given user id.
     *
     * @param userId           the id of the user to be updated
     * @param updateRequestDTO the {@link UsersUpdateRequestDTO} containing the new user details
     * @return a {@link GeneralResponseDTO} indicating the status of the request
     * @throws NotFoundException            if the user is not found
     * @throws InternalServerErrorException if any unexpected error occurs
     * @throws NotAuthorizedException       if the user is not authorized
     * @throws BadRequestException          if the request is invalid
     */
    @Override
    public GeneralResponseDTO updateUserDetails(Long userId, UsersUpdateRequestDTO updateRequestDTO)
            throws NotFoundException,
            InternalServerErrorException,
            NotAuthorizedException,
            BadRequestException {
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

            if (updateRequestDTO.currencyId() != null) {
                Currency currency = currencyRepository
                        .findById(updateRequestDTO.currencyId())
                        .orElseThrow(() -> new NotFoundException("Currency with id " + updateRequestDTO.currencyId() + " not found"));
                user.setCurrency(currency);
            }

            log.info("Updating details for user with id {}", userId);
            userRepository.save(user);

            return GeneralResponseDTO.builder()
                    .status(HttpStatus.OK)
                    .message("User details updated successfully")
                    .build();

        } catch (NotFoundException e) {
            log.error("User not found for user {}", userId, e);
            throw e;
        } catch (NotAuthorizedException e) {
            log.error("User not authorized for user {}", userId, e);
            throw e;
        } catch (BadRequestException e) {
            log.error("Password updated failed {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Error while getting user details", e);
            throw new InternalServerErrorException(
                    "An internal server error occurred: " + e.getMessage());
        }
    }

    private User validateUserAccess(Long userId) throws NotAuthorizedException, NotFoundException {
        log.info("Getting user details for {}", userId);
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        log.info("Validating access for user {}", userId);
        jwtUtil.validateUserAccess(user.getUsername());

        return user;
    }
}
