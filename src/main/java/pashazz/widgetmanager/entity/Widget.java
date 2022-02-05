package pashazz.widgetmanager.entity;

import java.time.LocalDateTime;

public interface Widget<T> extends ZOrderable {
  T getId();

  int getX();

  int getY();

  int getZ();

  int getWidth();

  int getHeight();

  LocalDateTime getLastUpdatedAt();
}
