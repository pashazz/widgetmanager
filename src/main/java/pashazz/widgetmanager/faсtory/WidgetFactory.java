package pashazz.widgetmanager.faсtory;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.impl.WidgetImpl;
import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;

public interface WidgetFactory<T> {

  /**
   * Creates a new widget, assigning an ID, based on createQuery
   * @return a newly created widget
   * @throws pashazz.widgetmanager.exception.WidgetCreationException if the data supplied is invalid (null objects) or NPE if the query itself is null
   *
   * If createQuery's Z is null, then Z-order is assigned to be defaultZ
   */
  @NotNull
  WidgetImpl<T> createNewWidget(@NotNull WidgetUpdateRequest createQuery, int defaultZ);

  /**
   * Updates a widget, based on info provided in {@link WidgetUpdateRequest}
   * @param existingWidget a widget to update
   * @param updateQuery data to be updated. Null parameters are skipped.
   * @return
   */
  @NotNull
  WidgetImpl<T> updateWidget(@NotNull WidgetImpl<T> existingWidget, @NotNull WidgetUpdateRequest updateQuery);
}
