package pashazz.widgetmanager.entity.db;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.MutableWidget;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@ToString // it's ok, we don't use lazy load here
@Table(name = "widgets",
  indexes = @Index(name = "zOrderIndex", columnList = "z ASC"))
public class JpaWidgetImpl implements MutableWidget<Long> {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;
  @Column(nullable = false)
  private int x;
  @Column(nullable = false)
  private int y;
  @Column(nullable = false)
  private int z;
  @Column(nullable = false)
  private int width;
  @Column(nullable = false)
  private int height;
  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  /**
   * This constructor is used by Hibernate
   **/
  public JpaWidgetImpl() {

  }

  JpaWidgetImpl(int x, int y, int z, int width, int height, @NotNull LocalDateTime lastUpdatedAt) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.width = width;
    this.height = height;
    this.lastUpdatedAt = lastUpdatedAt;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getZ() {
    return z;
  }

  @Override
  public void setZ(int z) {
    this.z = z;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public LocalDateTime getLastUpdatedAt() {
    return lastUpdatedAt;
  }

  @Override
  public void setLastUpdatedAt(@NotNull LocalDateTime lastUpdatedAt) {
    this.lastUpdatedAt = lastUpdatedAt;
  }
}
