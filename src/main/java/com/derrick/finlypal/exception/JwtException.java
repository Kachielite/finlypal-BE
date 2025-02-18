package com.derrick.finlypal.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtException {
    public static void handleException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setContentType("application/json");

        Map<String, Object> errors = new HashMap<>();
        errors.put("error", message);

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.writeValue(response.getOutputStream(), errors);
    }
}
