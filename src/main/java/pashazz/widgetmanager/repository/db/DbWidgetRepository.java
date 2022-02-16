package pashazz.widgetmanager.repository.db;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import pashazz.widgetmanager.entity.db.JpaWidgetImpl;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.exception.WidgetNotFoundException;
import pashazz.widgetmanager.factory.interfaces.WidgetFactory;
import pashazz.widgetmanager.repository.WidgetRepository;
import pashazz.widgetmanager.repository.db.jpa.JpaWidgetRepository;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
public class DbWidgetRepository implements WidgetRepository<Long> {

  private final JpaWidgetRepository repo;
  private final WidgetFactory<Long, JpaWidgetImpl> widgetFactory;
  private final EntityManager em;

  public DbWidgetRepository(JpaWidgetRepository repo,
                            WidgetFactory<Long, JpaWidgetImpl> widgetFactory,
                            EntityManager em) {
    this.repo = repo;
    this.widgetFactory = widgetFactory;
    this.em = em;
  }

  @Override
  @Transactional
  public @NotNull Widget<Long> createWidget(@NotNull WidgetUpdateRequest request) {
    var widget = widgetFactory.createNewWidget(request, getDefaultZ());
    shiftZ(widget.getZ());
    return repo.save(widget);
  }

  @Override
  @Transactional
  public @NotNull Widget<Long> updateWidget(@NotNull Long id, @NotNull WidgetUpdateRequest request) {
    var widget = _getWidget(id);
    int oldZ = widget.getZ();
    // here, we use the entity manager to make widget unmanaged to avoid side effects on Z order calculation
    em.detach(widget);
    var newWidget = widgetFactory.updateWidget(widget, request);
    if (oldZ != newWidget.getZ()) {
      shiftZ(newWidget.getZ());
    }

    return repo.save(newWidget);
  }

  @Override
  @Transactional
  public @NotNull Widget<Long> getWidget(@NotNull Long id) {
    return _getWidget(id);
  }

  @Override
  @Transactional
  public @NotNull List<Widget<Long>> listWidgets() {
    return Collections.unmodifiableList(repo.findAllOrderByZAsc());
  }


  @Override
  @Transactional
  public @NotNull List<Widget<Long>> listWidgets(int page, int pageSize) {
    return Collections.unmodifiableList(repo.findAllOrderByZAsc(Pageable.ofSize(pageSize).withPage(page)).toList());
  }

  @Override
  @Transactional
  public void deleteWidget(@NotNull Long id) {
    try {
      repo.deleteById(id);
    } catch (Exception e) {
      log.debug("Unable to delete widget by id {}: {}", id, e.getMessage());
    }
  }


  /**
   * Will shift current widget to the next zOrder until there is a gap or end of the list
   * <p>
   * Normally would require a repeatable read transaction isolation, but we
   * lock ourselves manually so don't care
   *
   * @param z z order that needs to be shifted + 1
   */

  private void shiftZ(int z) {
    shiftZAlgo(repo.findByZ(z));
  }

  /**
   * This will first find a widget with index == z + 1 and then increment widgetOpt's zIndex
   *
   * @param widgetOpt
   */
  private void shiftZAlgo(@NotNull Optional<JpaWidgetImpl> widgetOpt) {
    if (widgetOpt.isEmpty()) {
      return;
    }
    int z = widgetOpt.get().getZ();
    log.trace("shifting zOrder at widget: {}: {} -> {}", widgetOpt.get(), z, z + 1);
    Optional<JpaWidgetImpl> widgetOptNext = repo.findByZ(z + 1);

    widgetOpt.get().setZ(z + 1);
    repo.save(widgetOpt.get());

    shiftZAlgo(widgetOptNext);

  }

  private @NotNull JpaWidgetImpl _getWidget(@NotNull Long id) {
    return repo.findById(id).orElseThrow(() -> new WidgetNotFoundException(id.toString()));
  }

  private int getDefaultZ() {
    return Optional.ofNullable(repo.getTopZ()).orElse(0) + 1;
  }
}
