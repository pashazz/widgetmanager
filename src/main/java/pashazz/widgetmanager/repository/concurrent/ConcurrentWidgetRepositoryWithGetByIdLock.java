package pashazz.widgetmanager.repository.concurrent;

import org.jetbrains.annotations.NotNull;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.repository.WidgetRepository;

/**
 * This class additionally to ConcurrentWidgetRepostory puts the read lock on getById.
 * This is for guaranteed internal map structure of {@link pashazz.widgetmanager.repository.memory.InMemoryWidgetRepository}
 * <p>
 * the getWidget operation is being locked by our ReadLock because we do not want access to our Map during writing
 * However, with DB we don't care. See
 * We can't use ConcurrentHashMap +  no read lock  on getWidget because there are internal transitions when 2 widgets have the same zOrder in our
 * HashMap, which breaks the invariant of zOrder being unique. We need the whole update operation to be atomic
 *
 * @param <T>
 */
public class ConcurrentWidgetRepositoryWithGetByIdLock<T> extends ConcurrentWidgetRepository<T> {
  public ConcurrentWidgetRepositoryWithGetByIdLock(WidgetRepository<T> repo) {
    super(repo);
  }

  @Override
  public @NotNull Widget<T> getWidget(@NotNull T id) {
    return readSafely(() -> super.getWidget(id));
  }
}
