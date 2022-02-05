package pashazz.widgetmanager.rest.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
  @NonNull String type;
  @NonNull String message;
}
