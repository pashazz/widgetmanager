package pashazz.widgetmanager.factory;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.entity.interfaces.WidgetBuilder;
import pashazz.widgetmanager.entity.validator.RequestValidator;
import pashazz.widgetmanager.factory.interfaces.WidgetFactory;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This is an implementation of services responsible for creation and updating of widgets
 * depends on  a Builder supplier that specifies the kind of widget to create
 * This implementation will create a new object when updateWidget is called
 */
@Slf4j
public class StandardWidgetFactory<ID, T extends Widget<ID>>
  implements WidgetFactory<ID, T> {

  // this is used to assign IDs upon creation
  private final Supplier<ID> idGenerator;
  // this is used to validate incoming requests
  private final RequestValidator creationValidator;

  // and this one is for updates
  private final RequestValidator updateValidator;

  //this is used to generate builders for our widget objects
  private final Supplier<WidgetBuilder<ID, T>> builderSupplier;


  public StandardWidgetFactory(Supplier<ID> idGenerator,
                               RequestValidator creationValidator,
                               RequestValidator updateValidator,
                               Supplier<WidgetBuilder<ID, T>> builderSupplier) {
    this.idGenerator = idGenerator;
    this.creationValidator = creationValidator;
    this.updateValidator = updateValidator;
    this.builderSupplier = builderSupplier;
  }


  @Override
  public @NotNull T createNewWidget(@NotNull WidgetUpdateRequest request, int defaultZ) {
    ID id = idGenerator.get();
    log.debug("[{}]: creating new widget; request: {}", id, request);
    creationValidator.validate(request);
    return builderSupplier.get()
      .id(idGenerator.get())
      .x(request.getX())
      .y(request.getY())
      .z(Optional.ofNullable(request.getZ()).orElse(defaultZ))
      .width(request.getWidth())
      .height(request.getHeight())
      .lastUpdatedAt(LocalDateTime.now())
      .build();
  }

  @Override
  public @NotNull T updateWidget(@NotNull T existingWidget, @NotNull WidgetUpdateRequest request) {
    log.debug("[{}]: updating widget; request: {}", existingWidget.getId(), request);
    updateValidator.validate(request);
    return builderSupplier.get()
      .id(existingWidget.getId())
      .x(Optional.ofNullable(request.getX()).orElse(existingWidget.getX()))
      .y(Optional.ofNullable(request.getY()).orElse(existingWidget.getY()))
      .z(Optional.ofNullable(request.getZ()).orElse(existingWidget.getZ()))
      .width(Optional.ofNullable(request.getWidth()).orElse(existingWidget.getWidth()))
      .height(Optional.ofNullable(request.getHeight()).orElse(existingWidget.getHeight()))
      .lastUpdatedAt(LocalDateTime.now())
      .build();
  }

  protected RequestValidator getUpdateValidator() {
    return updateValidator;
  }


}
