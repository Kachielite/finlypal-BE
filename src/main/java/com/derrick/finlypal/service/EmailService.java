package com.derrick.finlypal.service;

import com.derrick.finlypal.exception.InternalServerErrorException;

public interface EmailService {
    void sendEmail(String to, String subject, String body) throws InternalServerErrorException;
}
