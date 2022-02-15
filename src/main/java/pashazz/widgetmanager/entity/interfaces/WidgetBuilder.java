package pashazz.widgetmanager.entity.interfaces;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * An interface to abstract Widget Builder.
 * Used by WidgetFactory.
 *
 * @param <T>  Widget type
 * @param <ID> Widget ID type
 */
public interface WidgetBuilder<ID, T extends Widget<ID>> {

  @NotNull
  T build();

  @NotNull
  WidgetBuilder<ID, T> id(@NotNull ID id);

  @NotNull
  WidgetBuilder<ID, T> x(int x);

  @NotNull
  WidgetBuilder<ID, T> y(int y);

  @NotNull
  WidgetBuilder<ID, T> z(int z);

  @NotNull
  WidgetBuilder<ID, T> height(int height);

  @NotNull
  WidgetBuilder<ID, T> width(int width);

  @NotNull
  WidgetBuilder<ID, T> lastUpdatedAt(@NotNull LocalDateTime lastUpdatedAt);


}
