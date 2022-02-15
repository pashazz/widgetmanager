package pashazz.widgetmanager.repository;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.exception.WidgetNotFoundException;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import java.util.List;

public interface WidgetRepository<T> {

  @NotNull
  Widget<T> createWidget(@NotNull WidgetUpdateRequest request);


  /**
   * Update a widget by id
   *
   * @param id      widget id
   * @param request what needs to be updated. Fields should be null if should have remained
   * @return updated widget
   * @throws WidgetNotFoundException if not found
   */
  @NotNull
  Widget<T> updateWidget(@NotNull T id, @NotNull WidgetUpdateRequest request);

  /**
   * Get a widget by id
   *
   * @return
   * @throws WidgetNotFoundException if not found
   */
  @NotNull
  Widget<T> getWidget(@NotNull T id);

  /**
   * Get a collection of widgets sorted by their Z-order
   *
   * @return
   */
  @NotNull
  List<Widget<T>> listWidgets();

  /**
   * get a page of widgets sorted by their Z-order
   *
   * @param page     current page number starting with 0
   * @param pageSize page size
   * @return collection with at most pageSize size
   */
  @NotNull
  List<Widget<T>> listWidgets(int page, int pageSize);

  void deleteWidget(@NotNull T id);

}
