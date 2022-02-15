package pashazz.widgetmanager.entity.memory;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.AbstractWidgetBuilder;
import pashazz.widgetmanager.entity.interfaces.Widget;

public class InMemoryLongWidgetBuilder extends AbstractWidgetBuilder<Long, Widget<Long>> {
  @Override
  public @NotNull Widget<Long> build() {
    return new InMemoryWidgetImpl<>(
      id,
      x,
      y,
      z,
      width,
      height,
      lastUpdatedAt
    );

  }
}
