package pashazz.widgetmanager.faсtory;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pashazz.widgetmanager.entity.impl.WidgetImpl;
import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;
import pashazz.widgetmanager.exception.WidgetCreationException;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * This is an implementation of services responsible for creation and updating of widgets
 * Not thread safe
 *
 */
@Service
@Slf4j
public class LongWidgetFactory implements WidgetFactory<Long> {

  private final Supplier<Long> idGenerator;

  public LongWidgetFactory(@Autowired Supplier<Long> idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public @NotNull WidgetImpl<Long> createNewWidget(@NotNull WidgetUpdateRequest createQuery, int defaultZ) {
    Long id = idGenerator.get();
    log.debug("[{}]: creating new widget; query: {}", id, createQuery);
    validateQuery(createQuery);
    return new WidgetImpl<>(id, createQuery, defaultZ);

  }

  @Override
  public @NotNull WidgetImpl<Long> updateWidget(@NotNull WidgetImpl<Long> existingWidget, @NotNull WidgetUpdateRequest query) {
   return new WidgetImpl<>(existingWidget, query);

  }

   //region implementation details


      /**
     * Will throw WidgetCreationException with validation error if
     * required values won't meet the requirements or null
     * @param query
     */
    private static void validateQuery(@NotNull WidgetUpdateRequest query) {
      requireNotNullInteger(query.getX(), "x");
      requireNotNullInteger(query.getY(), "y");
      requireNotNullAndPositiveInteger(query.getWidth(), "width");
      requireNotNullAndPositiveInteger(query.getHeight(), "height");

    }

  private static void requireNotNullAndPositiveInteger(Integer value, String name) {
      require(value, v -> v != null && v > 0, format("%s: expected: not null positive integer; got: %s", name, value));
  }

  private static <V> void  require(V value, Predicate<V> predicate, String exceptionDescription) {
      if (!predicate.test(value)) {
        throw new WidgetCreationException(exceptionDescription);
      }
    }

  private static void requireNotNullInteger(Integer value, String name) {
    require(value, Objects::nonNull, format("%s: expected: not null integer; got: null",  name));
  }
  //endregion
}
