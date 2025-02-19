package com.derrick.finlypal.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class InputValidation {
  public Map<String, String> validate(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = new HashMap<>();
      bindingResult
          .getFieldErrors()
          .forEach(
              error -> {
                errors.put(error.getField(), error.getDefaultMessage());
              });

      return errors;
    }

    return null; // No validation errors
  }
}
