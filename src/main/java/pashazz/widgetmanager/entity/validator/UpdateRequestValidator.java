package pashazz.widgetmanager.entity.validator;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

public class UpdateRequestValidator implements RequestValidator {
  @Override
  public void validate(@NotNull WidgetUpdateRequest request) {
    ValidationUtils.requireNullOrPositiveInteger(request.getHeight(), "height");
    ValidationUtils.requireNullOrPositiveInteger(request.getWidth(), "width");

  }
}
