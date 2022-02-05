package pashazz.widgetmanager.repository.memory;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pashazz.widgetmanager.aspect.annotation.Measure;
import pashazz.widgetmanager.entity.Widget;
import pashazz.widgetmanager.entity.impl.WidgetImpl;
import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;
import pashazz.widgetmanager.exception.WidgetNotFoundException;
import pashazz.widgetmanager.exception.WidgetPageException;
import pashazz.widgetmanager.faсtory.WidgetFactory;
import pashazz.widgetmanager.repository.WidgetRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.String.format;
import static pashazz.widgetmanager.utils.Utils.getInsertionPosition;

/**
 * This repository provides services for accessing and updating the collection of widgets
 *
 * This class is NOT thread safe
 * Thread-safety capabilities: concurrent read access to listWidgets method (widgetsById)
 * due to volatility of that variable
 */
@Service
@Slf4j
public class InMemoryWidgetRepository  implements WidgetRepository<Long> {

  /**
   * Sorted list of widget ids by z-order
   */
  private final  ArrayList<WidgetImpl<Long>> widgetsByZOrder;
  private final HashMap<Long, WidgetImpl<Long>> widgetsById;

  /**
   * This variable contains a list of widgets that our read operation uses
   * This list contains only thread-safe objects
   * Controlled by {@link #commitNewVersion()}
   */
  private volatile List<Widget<Long>> committedVersion;

  WidgetFactory<Long> widgetFactory;

  public InMemoryWidgetRepository(
    @Autowired WidgetFactory<Long> widgetFactory
  ) {
    this.widgetFactory = widgetFactory;
    widgetsById = new HashMap<>();
    widgetsByZOrder = new ArrayList<>();
    committedVersion = Collections.emptyList();
  }


  @Override
  @Measure
  @NotNull
  public  Widget<Long> createWidget(@NotNull WidgetUpdateRequest query) {
    var widget = widgetFactory.createNewWidget(query, getDefaultZ());
    makeWidgetCreate(widget);
    log.info("[{}]: CREATED with zOrder: {}", widget.getId(), widget.getZ());
    commitNewVersion();
    return widget;
  }


  @Override
  @Measure
  @NotNull
  public Widget<Long> updateWidget(Long id, @NotNull WidgetUpdateRequest query) {
    WidgetImpl<Long> oldWidget = widgetsById.get(id);
    WidgetImpl<Long> newWidget = widgetFactory.updateWidget(oldWidget, query);
    makeWidgetUpdate(oldWidget, newWidget);
    log.info("[{}]: UPDATED with zOrder: {} -> {}", oldWidget.getId(), oldWidget.getZ(), newWidget.getZ());
    commitNewVersion();
    return newWidget;
  }

  /**
   * This method does under the hood work of injecting a widget new to our data structures
   *
   */
  void makeWidgetCreate(WidgetImpl<Long> widget) {
    addWidgetToIdMap(widget);
    int zIndex = indexOfZOrder(widget.getZ());
    updateZOrderArrayWithWidget(widget, zIndex);
  }

  /**
   * This method replaces one widget with another in our data structures during update operation
   * @param oldWidget before update
   * @param newWidget after update
   */
  void makeWidgetUpdate(WidgetImpl<Long> oldWidget, WidgetImpl<Long> newWidget) {
    addWidgetToIdMap(newWidget);
    int oldZOrderIndex = indexOfZOrder(oldWidget.getZ());
    if (oldWidget.getZ() == newWidget.getZ()) {
      log.debug("[{}]: z-order is not changed: {}, replacing the object in the z-order list", oldWidget.getId(), oldWidget.getZ());
      replaceWidgetAtIndex(oldZOrderIndex, newWidget);
      return;
    }

    removeWidgetAtIndex(oldZOrderIndex);
    int newZOrderIndex = indexOfZOrder(newWidget.getZ());
    updateZOrderArrayWithWidget(newWidget, newZOrderIndex);
  }

  private void removeWidgetAtIndex(int zOrderIndex) {
    widgetsByZOrder.remove(zOrderIndex);
  }

  private void replaceWidgetAtIndex(int oldZOrderIndex, WidgetImpl<Long> newWidget) {
    widgetsByZOrder.set(oldZOrderIndex, newWidget);
  }

  protected void addWidgetToIdMap(WidgetImpl<Long> widget) {
    widgetsById.put(widget.getId(), widget);
  }



  /**
   * Commits (publishes) the new version of changes
   *
   * Once we commit new version, we change the link that committedVersion possesses and replace it with the new
   * shallow copy of our thread-safe non-mutable widgets
   *
   * volatility of committedVersion ensures happens-before guarantee
   */
  protected void commitNewVersion() {
    committedVersion = Collections.unmodifiableList((List<Widget<Long>>)widgetsByZOrder.clone());
    log.info("new version committed at {}", LocalDateTime.now());
  }



  @Override
  public @NotNull Widget<Long> getWidget(@NotNull Long id) {
    return _getWidget(id);
  }

  @Override
  public @NotNull List<Widget<Long>> listWidgets() {
    return committedVersion;
  }

  @Override
  public @NotNull List<Widget<Long>> listWidgets(int page, int pageSize) {
    int startIndex = page*pageSize;
    try {
      return Collections.unmodifiableList(committedVersion.subList(startIndex, Math.min(startIndex + pageSize, committedVersion.size())));
    } catch (IndexOutOfBoundsException e) {
      throw new WidgetPageException(format("page %s is out of bounds with size %s; output size: %s", page, pageSize, committedVersion.size()));
    }
  }

  @Override
  @Measure
  public void deleteWidget(@NotNull Long id) {
    var widget = widgetsById.remove(id);
    if (widget == null) {
      log.debug("deleteWidget: id not found: {}", id);
      return;
    }

    int index = indexOfZOrder(widget.getZ());
    log.debug("[{}]: deleting from position {}",widget.getId(), index);
    removeWidgetAtIndex(index);
    commitNewVersion();
  }


  private @NotNull WidgetImpl<Long> _getWidget(@NotNull Long id) {
    return Optional.ofNullable(widgetsById.get(id))
      .orElseThrow(() -> new WidgetNotFoundException(id.toString()));
  }


  private int getDefaultZ() {
    if (widgetsByZOrder.size() == 0) {
      return 0;
    }
    return widgetsByZOrder.get(widgetsByZOrder.size() - 1).getZ() + 1;
  }

  /**
   * returns an index where element with this z-order resides, or
   * if the number is negative, the element where the element with this z-order needs to be inserted
   * @param z
   * @return
   */
  private int indexOfZOrder(int z) {
    //This lambda implements ZOrderable like Widget<T>
    return Collections.binarySearch(widgetsByZOrder, () -> z);

  }

  private void updateZOrderArrayWithWidget(@NotNull WidgetImpl<Long> widget, int index) {
    int z = widget.getZ();

    if (index >=  0) {
      log.debug("[{}]: updateZOrderWithWidget: existing widget found with zOrder {} at position {}, shifting: {}",widget.getId(), z, index, widgetsByZOrder.get(index));
      // we want element at position index to have zOrder = it's current zOrder + 1
      shiftList(z+1, index);
      log.debug("[{}]: inserting widget at position {} after shifting existing zOrder {}", widget.getId(), index, z);
      widgetsByZOrder.add(index, widget);
    } else { //negative index means that no element with this zOrder is found, but a position to insert is found
            // -> a gap in zOrders. No need to shift
      int insertionPosition = getInsertionPosition(index);
      log.debug("[{}]: inserting widget at position {}, no zOrder {} found", widget.getId(), insertionPosition, z);
      widgetsByZOrder.add(insertionPosition, widget);
    }
  }


  /**
 =
   * Will shift current widget to the next zOrder until there is a gap or end of the list
   * @param zOrder desirable zOrder at position pos
   * @param pos a correct position in the array
   */
  private void shiftList(int zOrder, int pos) {
    log.trace("shifting zOrder at position {}: {}", pos, zOrder);
    var widget = widgetsByZOrder.get(pos);
    var newWidget = new WidgetImpl<>(
        widget,
        WidgetUpdateRequest.builder()
          .z(zOrder)
          .build()
      );
    replaceWidgetAtIndex(pos, newWidget);
    addWidgetToIdMap(newWidget);
    if (pos + 1 < widgetsByZOrder.size() && widgetsByZOrder.get(pos + 1).getZ() == zOrder) {
      shiftList(zOrder + 1, pos + 1);
    }
  }
}
