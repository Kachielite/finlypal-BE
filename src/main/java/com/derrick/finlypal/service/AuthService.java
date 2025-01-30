package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.*;
import com.derrick.finlypal.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface AuthService {
    AuthenticationResponseDTO login(AuthenticationRequestDTO authenticationRequestDTO)
            throws InternalServerErrorException, NotFoundException, BadCredentialsException;

    AuthenticationResponseDTO register(UsersRegistrationRequestDTO usersRegistrationRequestDTO)
            throws UserAlreadyExistsException, InternalServerErrorException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws NotFoundException, InternalServerErrorException;

    GeneralResponseDTO getPasswordRequestToken(String email)
            throws InternalServerErrorException, BadRequestException;

    GeneralResponseDTO resetPassword(String token, UsersUpdateRequestDTO usersUpdateRequestDTO)
            throws InternalServerErrorException, BadRequestException, NotAuthorizedException, NotFoundException;
}
