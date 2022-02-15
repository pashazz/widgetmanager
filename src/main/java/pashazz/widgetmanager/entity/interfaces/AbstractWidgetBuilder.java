package pashazz.widgetmanager.entity.interfaces;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public abstract class AbstractWidgetBuilder<ID, T extends Widget<ID>>
  implements WidgetBuilder<ID, T> {


  protected ID id;

  protected int x;

  protected int y;

  protected int z;

  protected int height;

  protected int width;

  protected LocalDateTime lastUpdatedAt;

  @Override
  @NotNull
  public abstract T build();


  @Override
  public @NotNull WidgetBuilder<ID, T> id(@NotNull ID id) {
    this.id = id;
    return this;
  }

  @Override
  public @NotNull WidgetBuilder<ID, T> x(int x) {
    this.x = x;
    return this;
  }

  @Override
  public @NotNull WidgetBuilder<ID, T> y(int y) {
    this.y = y;
    return this;
  }

  @Override
  public @NotNull WidgetBuilder<ID, T> z(int z) {
    this.z = z;
    return this;
  }

  @Override
  public @NotNull WidgetBuilder<ID, T> height(int height) {
    this.height = height;
    return this;
  }

  @Override
  public @NotNull WidgetBuilder<ID, T> width(int width) {
    this.width = width;
    return this;
  }

  @Override
  public @NotNull WidgetBuilder<ID, T> lastUpdatedAt(LocalDateTime lastUpdatedAt) {
    this.lastUpdatedAt = lastUpdatedAt;
    return this;
  }
}
