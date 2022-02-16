package pashazz.widgetmanager.factory.interfaces;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

public interface WidgetFactory<ID, T extends Widget<ID>> {

  /**
   * Creates a new widget, assigning an ID, based on request
   * <p>
   * If request's Z is null, then Z-order is assigned to be defaultZ. Other values should be validated to be non-null
   *
   * @return a newly created widget
   * @throws pashazz.widgetmanager.exception.WidgetCreationException if the data supplied is invalid (null objects) or NPE if the query itself is null
   */
  @NotNull
  T createNewWidget(@NotNull WidgetUpdateRequest request, int defaultZ);

  /**
   * Updates a widget, based on info provided in {@link WidgetUpdateRequest}
   *
   * @param existingWidget a widget to update
   * @param request        data to be updated. Null parameters are skipped.
   * @return updated widget
   */
  @NotNull
  T updateWidget(@NotNull T existingWidget, @NotNull WidgetUpdateRequest request);
}
