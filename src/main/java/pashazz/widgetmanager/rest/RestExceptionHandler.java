package pashazz.widgetmanager.rest;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pashazz.widgetmanager.rest.response.ErrorResponse;

import java.util.Objects;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
    @NotNull HttpMessageNotReadableException ex, @NotNull HttpHeaders headers,
    @NotNull HttpStatus status, @NotNull WebRequest request) {
                return ResponseEntity.badRequest().body(ErrorResponse.builder()
                  .type(status.getReasonPhrase())
                  .message("HTTP Message not readable")
                  .build());
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(@NotNull Exception ex, Object body, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
    return ResponseEntity.badRequest().body(ErrorResponse.builder()
                  .type(status.getReasonPhrase())
                  .message("Internal Server Error")
                  .build());
  }
}
