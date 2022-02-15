package pashazz.widgetmanager.entity.validator;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import static pashazz.widgetmanager.entity.validator.ValidationUtils.requireNotNullAndPositiveInteger;
import static pashazz.widgetmanager.entity.validator.ValidationUtils.requireNotNullInteger;

@Service
public class CreationRequestValidator implements RequestValidator {

  /**
   * Will throw WidgetCreationException with validation error if
   * required values won't meet the requirements or null
   * <p>
   * The requirements are as follows:
   * x, y must be a valid integer
   * width, height must be a positive integer
   *
   * @param request request to validate
   */
  public void validate(@NotNull WidgetUpdateRequest request) {
    requireNotNullInteger(request.getX(), "x");
    requireNotNullInteger(request.getY(), "y");
    requireNotNullAndPositiveInteger(request.getWidth(), "width");
    requireNotNullAndPositiveInteger(request.getHeight(), "height");

  }


}
