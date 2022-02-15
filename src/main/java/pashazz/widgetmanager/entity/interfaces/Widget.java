package pashazz.widgetmanager.entity.interfaces;

import java.time.LocalDateTime;


public interface Widget<ID> extends ZOrderable {
  ID getId();

  int getX();

  int getY();

  int getZ();

  int getWidth();

  int getHeight();

  LocalDateTime getLastUpdatedAt();
}
