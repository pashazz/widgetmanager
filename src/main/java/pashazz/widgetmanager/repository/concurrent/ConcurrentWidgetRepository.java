package pashazz.widgetmanager.repository.concurrent;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.aspect.annotation.Measure;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.repository.WidgetRepository;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * This is a decorator around InMemoryWidgetRepository that locks the write operations.
 * The assumptions are the following:
 * 1. Our InMemoryWidgetRepository has an internal list of Z-ordered widgets and a map id -> widget
 * 2. Our WriteLock locks write operations to prevent concurrent access to both the list and the map
 * 3. The listWidgets() operation is thread-safe in InMemoryWidgetRepository due to the underlying variable being volatile
 * and it is only accessed by us through assignment, so no locks
 * 4. the getWidget operation is being locked by our ReadLock because we do not want access to our Map during writing
 * 5. We can't use ConcurrentHashMap +  no read lock  on getWidget because there are internal transitions when 2 widgets have the same zOrder in our
 * HashMap, which breaks the invariant of zOrder being unique. We need the whole update operation to be atomic
 * <p>
 * <p>
 * The assumption on database is that we always get a consistent listWidgets list (isolation level read-committed) and therefore it won't need a lock.
 * <p>
 */
public class ConcurrentWidgetRepository<T> implements WidgetRepository<T> {

  private final WidgetRepository<T> repo;

  private final ReadWriteLock lock;

  public ConcurrentWidgetRepository(WidgetRepository<T> repo) {
    this.repo = repo;
    this.lock = new ReentrantReadWriteLock();
  }

  @Measure
  @Override
  public @NotNull Widget<T> createWidget(@NotNull WidgetUpdateRequest request) {
    return writeSafely(() -> repo.createWidget(request));
  }

  @Measure
  @Override
  public @NotNull Widget<T> updateWidget(@NotNull T id, @NotNull WidgetUpdateRequest request) {
    return writeSafely(() -> repo.updateWidget(id, request));
  }

  @Measure
  @Override
  public @NotNull Widget<T> getWidget(@NotNull T id) {
    return readSafely(() -> repo.getWidget(id));
  }

  @Measure
  @Override
  public @NotNull List<Widget<T>> listWidgets() {
    return repo.listWidgets();
  }

  @Measure
  @Override
  public @NotNull List<Widget<T>> listWidgets(int page, int pageSize) {
    return repo.listWidgets(page, pageSize);
  }

  @Measure
  @Override
  public void deleteWidget(@NotNull T id) {
    writeSafely(() -> {
      repo.deleteWidget(id);
      return null;
    });
  }

  private void acquireReadLock() {
    lock.readLock().lock();
  }

  private void acquireWriteLock() {
    lock.writeLock().lock();
  }

  private void releaseReadLock() {
    lock.readLock().unlock();
  }

  private void releaseWriteLock() {
    lock.writeLock().unlock();
  }

  private <O> O readSafely(@NotNull Supplier<O> execute) {
    acquireReadLock();
    try {
      return execute.get();
    } finally {
      releaseReadLock();
    }
  }

  private <O> O writeSafely(@NotNull Supplier<O> execute) {
    acquireWriteLock();
    try {
      return execute.get();
    } finally {
      releaseWriteLock();
    }
  }
}
