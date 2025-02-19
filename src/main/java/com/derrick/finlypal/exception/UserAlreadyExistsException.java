package com.derrick.finlypal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    value = HttpStatus.CONFLICT,
    reason = "A user with the provided email already exists")
public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String email) {
    super("User with email " + email + " already exists");
  }
}
