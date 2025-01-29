package com.derrick.finlypal.util;

import com.derrick.finlypal.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@Component
public class InputValidation {
    Map<String, String> errors = new HashMap<>();

    public ResponseEntity<ErrorResponseDTO> validate(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            // Return bad request with validation errors
            return new ResponseEntity<>(
                    ErrorResponseDTO.builder()
                            .message("Validation error")
                            .errors(errors)
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        return null;
    }
}
