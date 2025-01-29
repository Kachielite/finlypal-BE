package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.AuthenticationRequestDTO;
import com.derrick.finlypal.dto.AuthenticationResponseDTO;
import com.derrick.finlypal.dto.UsersRegistrationRequestDTO;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotFoundException;
import com.derrick.finlypal.exception.UserAlreadyExistsException;
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
}
