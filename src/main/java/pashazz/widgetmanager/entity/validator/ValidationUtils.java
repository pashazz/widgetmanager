package pashazz.widgetmanager.entity.validator;

import pashazz.widgetmanager.exception.WidgetCreationException;

import java.util.Objects;
import java.util.function.Predicate;

import static java.lang.String.format;

public class ValidationUtils {
  public static void requireNotNullAndPositiveInteger(Integer value, String name) {
    require(value, v -> v != null && v > 0, format("%s: expected: not null positive integer; got: %s", name, value));
  }

  public static <V> void require(V value, Predicate<V> predicate, String exceptionDescription) {
    if (!predicate.test(value)) {
      throw new WidgetCreationException(exceptionDescription);
    }
  }

  public static void requireNotNullInteger(Integer value, String name) {
    require(value, Objects::nonNull, format("%s: expected: not null integer; got: null", name));
  }

  public static void requireNullOrPositiveInteger(Integer value, String name) {
    require(value, v -> v == null || v > 0, format("%s: expected: null or positive integer; got: %s", name, value));
  }
}
