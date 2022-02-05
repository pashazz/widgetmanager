package pashazz.widgetmanager.entity.impl;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.Widget;
import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

/**
 * This is an object representing widget on a plane.
 *
 * @param <T>
 */
@Slf4j
@EqualsAndHashCode(doNotUseGetters = true)
@Value
@ToString
public class WidgetImpl<T> implements Widget<T> {


  private T id;
  private int x;
  private int y;
  private int z;
  private int width;
  private int height;
  private LocalDateTime lastUpdatedAt;


  /**
   * Creates a new Widget. Won't do any checks, use WidgetFactory to construct a correct object
   */
  public WidgetImpl(@NotNull T id, @NotNull WidgetUpdateRequest query, int defaultZ) {
    log.debug("[{}]: creating a new Widget object with x={}, y={}, width={}, height={}, zOrder={}", id, query.getX(), query.getY(), query.getWidth(), query.getHeight(), query.getZ());
    this.id = Objects.requireNonNull(id);
    this.x = query.getX();
    this.y = query.getY();
    this.z = Optional.ofNullable(query.getZ()).orElse(defaultZ);
    this.width = query.getWidth();
    this.height = query.getHeight();
    this.lastUpdatedAt = LocalDateTime.now();
  }

  public WidgetImpl(@NotNull WidgetImpl<T> existingWidget, @NotNull WidgetUpdateRequest query) {
    log.debug("[{}]: updating a  Widget object with x={}, y={}, width={}, height={}, zOrder={}", existingWidget.getId(), query.getX(), query.getY(), query.getWidth(), query.getHeight(), query.getZ());
    this.id = existingWidget.id;
    this.x = Optional.ofNullable(query.getX()).orElse(existingWidget.x);
    this.y = Optional.ofNullable(query.getY()).orElse(existingWidget.y);
    this.z = Optional.ofNullable(query.getZ()).orElse(existingWidget.z);
    this.width = Optional.ofNullable(query.getWidth()).orElse(existingWidget.width);
    this.height = Optional.ofNullable(query.getHeight()).orElse(existingWidget.height);
    this.lastUpdatedAt = LocalDateTime.now();
  }
}

