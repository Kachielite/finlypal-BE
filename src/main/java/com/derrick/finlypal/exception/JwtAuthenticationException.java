package com.derrick.finlypal.exception;

public class JwtAuthenticationException extends RuntimeException {
  public JwtAuthenticationException(String message) {
    super(message);
  }
}
