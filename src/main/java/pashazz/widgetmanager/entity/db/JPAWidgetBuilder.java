package pashazz.widgetmanager.entity.db;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.AbstractWidgetBuilder;

public class JPAWidgetBuilder extends AbstractWidgetBuilder<Long, JpaWidgetImpl> {

  @Override
  public @NotNull JpaWidgetImpl build() {
    return new JpaWidgetImpl(x, y, z, width, height, lastUpdatedAt);
  }
}
