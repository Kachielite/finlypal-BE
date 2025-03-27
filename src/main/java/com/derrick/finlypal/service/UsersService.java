package com.derrick.finlypal.service;

import com.derrick.finlypal.dto.UsersResponseDTO;
import com.derrick.finlypal.dto.UsersUpdateRequestDTO;
import com.derrick.finlypal.exception.BadRequestException;
import com.derrick.finlypal.exception.InternalServerErrorException;
import com.derrick.finlypal.exception.NotAuthorizedException;
import com.derrick.finlypal.exception.NotFoundException;

public interface UsersService {
    UsersResponseDTO getUserDetails()
            throws NotFoundException,
            InternalServerErrorException,
            NotAuthorizedException,
            BadRequestException;

    UsersResponseDTO updateUserDetails(Long userId, UsersUpdateRequestDTO updateRequestDTO)
            throws NotFoundException,
            InternalServerErrorException,
            NotAuthorizedException,
            BadRequestException;
}
