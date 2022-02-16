package pashazz.widgetmanager.factory;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.MutableWidget;
import pashazz.widgetmanager.entity.interfaces.WidgetBuilder;
import pashazz.widgetmanager.entity.validator.RequestValidator;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This factory will mutate the received object if updateWidget is called
 *
 * @param <ID>
 * @param <T>
 */
@Slf4j
public class MutableWidgetFactory<ID, T extends MutableWidget<ID>> extends StandardWidgetFactory<ID, T> {

  public MutableWidgetFactory(Supplier<ID> idGenerator,
                              RequestValidator creationValidator,
                              RequestValidator updateValidator,
                              Supplier<WidgetBuilder<ID, T>> widgetBuilderSupplier) {
    super(idGenerator, creationValidator, updateValidator, widgetBuilderSupplier);
  }

  @Override
  public @NotNull T updateWidget(@NotNull T existingWidget, @NotNull WidgetUpdateRequest request) {
    getUpdateValidator().validate(request);
    log.debug("[{}]: updating mutable widget; request: {}", existingWidget.getId(), request);
    Optional.ofNullable(request.getX()).ifPresent(existingWidget::setX);
    Optional.ofNullable(request.getY()).ifPresent(existingWidget::setY);
    Optional.ofNullable(request.getZ()).ifPresent(existingWidget::setZ);
    Optional.ofNullable(request.getWidth()).ifPresent(existingWidget::setWidth);
    Optional.ofNullable(request.getHeight()).ifPresent(existingWidget::setHeight);
    existingWidget.setLastUpdatedAt(LocalDateTime.now());
    return existingWidget;
  }
}
