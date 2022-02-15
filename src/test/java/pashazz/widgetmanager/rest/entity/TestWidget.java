package pashazz.widgetmanager.rest.entity;

import lombok.Data;
import pashazz.widgetmanager.entity.interfaces.Widget;

import java.time.LocalDateTime;

/**
 * This class is used in web tests as entity
 */
@Data
public class TestWidget implements Widget<Long> {
  private Long id;
  private int x;
  private int y;
  private int z;
  private int width;
  private int height;
  private LocalDateTime lastUpdatedAt;

}
