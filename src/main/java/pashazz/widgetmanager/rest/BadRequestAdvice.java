package pashazz.widgetmanager.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pashazz.widgetmanager.exception.WidgetCreationException;
import pashazz.widgetmanager.exception.WidgetNotFoundException;
import pashazz.widgetmanager.exception.WidgetPageException;
import pashazz.widgetmanager.rest.response.ErrorResponse;

@RestControllerAdvice
public class BadRequestAdvice {

  @ExceptionHandler(WidgetCreationException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorResponse badRequest(WidgetCreationException ex) {
    return ErrorResponse.builder()
      .type(HttpStatus.BAD_REQUEST.getReasonPhrase())
      .message(ex.getMessage())
      .build();
  }

  @ExceptionHandler(WidgetNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorResponse notFound(WidgetNotFoundException ex) {
    return ErrorResponse.builder()
      .type(HttpStatus.NOT_FOUND.getReasonPhrase())
      .message(ex.getMessage())
      .build();
  }

   @ExceptionHandler(WidgetPageException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public ErrorResponse page(WidgetPageException ex) {
    return ErrorResponse.builder()
      .type(HttpStatus.NOT_FOUND.getReasonPhrase())
      .message(ex.getMessage())
      .build();
  }
}
