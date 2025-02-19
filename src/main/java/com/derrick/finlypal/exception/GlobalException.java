package com.derrick.finlypal.exception;

import com.derrick.finlypal.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    List<ObjectError> validationErrors = ex.getBindingResult().getAllErrors();

    for (ObjectError error : validationErrors) {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    }

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex, WebRequest request) {
    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .apiPath(request.getDescription(false))
            .code(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFoundException(
      NotFoundException ex, WebRequest request) {
    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .apiPath(request.getDescription(false))
            .code(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InternalServerErrorException.class)
  public ResponseEntity<ErrorResponseDTO> handleInternalServerErrorException(
      InternalServerErrorException ex, WebRequest request) {
    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .apiPath(request.getDescription(false))
            .code(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadRequestException(
      BadRequestException ex, WebRequest request) {
    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .apiPath(request.getDescription(false))
            .code(HttpStatus.BAD_REQUEST)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotAuthorizedException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotAuthorizedException(
      NotAuthorizedException ex, WebRequest request) {
    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .apiPath(request.getDescription(false))
            .code(HttpStatus.FORBIDDEN)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(JwtAuthenticationException.class)
  public ResponseEntity<ErrorResponseDTO> handleJwtAuthenticationException(
      JwtAuthenticationException ex, WebRequest request) {
    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .apiPath(request.getDescription(false))
            .code(HttpStatus.FORBIDDEN)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }
}
