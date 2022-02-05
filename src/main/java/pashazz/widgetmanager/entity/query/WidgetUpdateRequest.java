package pashazz.widgetmanager.entity.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * This is the arguments for the following methods
 * 1. Create new widget (all args bar Z must NOT be null)
 * 2. Update a widget (omitted args won't update)
 */
@Value
@AllArgsConstructor
@Builder
public class WidgetUpdateRequest {
  private Integer x;
  private Integer y;
  private Integer width;
  private Integer height;
  private Integer z;
}
