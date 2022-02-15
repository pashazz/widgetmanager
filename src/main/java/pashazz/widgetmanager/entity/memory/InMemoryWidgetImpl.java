package pashazz.widgetmanager.entity.memory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pashazz.widgetmanager.entity.interfaces.Widget;

import java.time.LocalDateTime;

/**
 * This is an object representing widget on a plane,
 * backed by in-memory storage
 * <p>
 * This object is immutable. For mutable implementation, look at {@link  pashazz.widgetmanager.entity.interfaces.MutableWidget} derivative(s)
 *
 * @param <T> Type of Widget ID
 */
@Slf4j
@EqualsAndHashCode(doNotUseGetters = true)
@Value
@ToString
@AllArgsConstructor
public class InMemoryWidgetImpl<T> implements Widget<T> {
  private T id;
  private int x;
  private int y;
  private int z;
  private int width;
  private int height;
  private LocalDateTime lastUpdatedAt;
}

