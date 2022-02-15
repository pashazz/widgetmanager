package pashazz.widgetmanager.entity.interfaces;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * This intefrace is implemented by mutable widgets,
 * i.e. those backed by Hibernate
 *
 * @param <ID>
 */
public interface MutableWidget<ID> extends Widget<ID> {
  void setX(int x);

  void setY(int y);

  void setZ(int z);

  void setHeight(int height);

  void setWidth(int width);

  void setLastUpdatedAt(@NotNull LocalDateTime lastUpdatedAt);
}
