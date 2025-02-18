package com.derrick.finlypal.exception;

import com.derrick.finlypal.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class JwtException {
    public static void handleException(HttpServletResponse response, String message, HttpStatus status, WebRequest request) throws IOException {
        response.setContentType("application/json");

        Map<String, Object> errors = new HashMap<>();
        errors.put("error", message);

        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .apiPath(request.getDescription(false))
                .code(status)
                .message(message)
                .timestamp(LocalDate.now())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponseDTO);
    }
}
