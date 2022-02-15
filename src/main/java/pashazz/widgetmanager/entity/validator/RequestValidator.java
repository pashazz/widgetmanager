package pashazz.widgetmanager.entity.validator;


import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

/**
 * Validates widget creation requests
 */
public interface RequestValidator {
  void validate(@NotNull WidgetUpdateRequest request);

}
