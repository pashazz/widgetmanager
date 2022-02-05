package pashazz.widgetmanager.repository.memory;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pashazz.widgetmanager.aspect.annotation.Measure;
import pashazz.widgetmanager.entity.Widget;
import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;
import pashazz.widgetmanager.repository.WidgetRepository;

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
 *
 * This decorator depends on implementation intricacies of InMemoryWidgetRepository and has been done so that we'd split locking/unlocking capabilities for
 * better testing
 *
 */
@Service
public class  ConcurrentInMemoryWidgetRepository implements WidgetRepository<Long> {


  private final InMemoryWidgetRepository repo;

  private final ReadWriteLock lock;

  public ConcurrentInMemoryWidgetRepository(@Autowired @NotNull InMemoryWidgetRepository repo) {
    this.repo = repo;
    this.lock = new ReentrantReadWriteLock();
  }

  @Override
  public @NotNull Widget<Long> createWidget(@NotNull WidgetUpdateRequest query) {
    return writeSafely(() -> repo.createWidget(query));
  }

  @Override
  public @NotNull Widget<Long> updateWidget(Long id, @NotNull WidgetUpdateRequest query) {
    return writeSafely(() -> repo.updateWidget(id, query));
  }

  @Override
  public @NotNull Widget<Long> getWidget(@NotNull Long id) {
    return readSafely(() -> repo.getWidget(id));
  }

  @Override
  public @NotNull List<Widget<Long>> listWidgets() {
    return repo.listWidgets();
  }

  @Override
  public @NotNull List<Widget<Long>> listWidgets(int page, int pageSize) {
    return repo.listWidgets(page, pageSize);
  }

  @Override
  public void deleteWidget(@NotNull Long id) {
    writeSafely(() -> {
      repo.deleteWidget(id);
      return null;
    });
  }

   @Measure
  private void acquireReadLock() {
    lock.readLock().lock();
  }

  @Measure
  private void acquireWriteLock() {
    lock.writeLock().lock();
  }

  @Measure
  private void releaseReadLock() {
    lock.readLock().unlock();
  }
  @Measure
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
