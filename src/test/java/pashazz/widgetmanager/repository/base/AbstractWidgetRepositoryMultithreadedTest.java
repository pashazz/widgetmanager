package pashazz.widgetmanager.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import pashazz.widgetmanager.WidgetmanagerApplication;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.repository.WidgetRepository;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = WidgetmanagerApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public abstract class AbstractWidgetRepositoryMultithreadedTest {

  @Autowired
  private WidgetRepository<Long> repo;

  private static WidgetUpdateRequest createCreationQuery() {
    return WidgetUpdateRequest.builder()
      .x(10)
      .y(20)
      .z(1)
      .height(100)
      .width(100)
      .build();
  }

  @Test
  @Timeout(value = 50, unit = TimeUnit.SECONDS)
  void shouldConcurrentlyChangeBy2GroupsOfThreads() throws InterruptedException {
    final int CREATOR_THREADS = 500;

    final CountDownLatch start = new CountDownLatch(1);

    //region 1. Create widgets concurrently
    final WidgetUpdateRequest query = createCreationQuery();
    final CountDownLatch doneCreating = createNSameWidgetsConcurrently(CREATOR_THREADS, query, start);
    //endregion
    final CountDownLatch doneChanging = new CountDownLatch(2 * CREATOR_THREADS);


    //region  2. Make 1st part of changes concurrently. Add +10 to their x, y, +20 to width and height
    var updateQuery = WidgetUpdateRequest
      .builder()
      .x(query.getX() + 10)
      .y(query.getY() + 10)
      .width(query.getWidth() + 20)
      .height(query.getWidth() + 20)
      .build();

    List<Long> ids = new ArrayList<>();

    CountDownLatch startEditing = new CountDownLatch(1);
    new Thread(() -> {
      try {
        doneCreating.await();
        ids.addAll(repo.listWidgets()
          .stream()
          .map(Widget::getId)
          .collect(Collectors.toList()));

        startEditing.countDown();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();

    for (int i = 0; i < CREATOR_THREADS; ++i) {
      final int I = i;
      Thread thread = new Thread(() -> {
        try {
          startEditing.await();
          Long id = ids.get(I);
          repo.updateWidget(id, updateQuery);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        doneChanging.countDown();
      });
      thread.start();
    }
    //endregion
    // region 3. Make 2nd part of changes concurrently to 1st: Add +100 to their x, y; +200 to width and height

    var updateQuery2 = WidgetUpdateRequest
      .builder()
      .x(query.getX() + 100)
      .y(query.getY() + 100)
      .width(query.getWidth() + 200)
      .height(query.getWidth() + 200)
      .build();
    for (int i = 0; i < CREATOR_THREADS; ++i) {
      final int I = i;
      Thread thread = new Thread(() -> {
        try {
          startEditing.await();
          Long id = ids.get(I);
          repo.updateWidget(id, updateQuery2);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        doneChanging.countDown();
      });
      thread.start();
    }
    //endregion

    //start the threads
    start.countDown();

    //wait for the threads
    doneChanging.await();

    //region Assert
    Collection<Widget<Long>> widgets = repo.listWidgets();

    // check that each widget was changed either by 1st part of the threads or the 2nd, with no stale values or in-between
    for (var widget : widgets) {
      assertTrue(compareWithQuery(widget, updateQuery) || compareWithQuery(widget, updateQuery2));
    }
  }

  @Test
  @Timeout(value = 50, unit = TimeUnit.SECONDS)
  void shouldConcurrentlyDeleteAndListUsingManyThreads() throws InterruptedException {
    var N = 100;
    CountDownLatch start = new CountDownLatch(1);
    var creationFinished = createNSameWidgetsConcurrently(N, createCreationQuery(), start);
    start.countDown();
    creationFinished.await();
    var allWidgets = repo.listWidgets();

    CountDownLatch deletionFinished = new CountDownLatch(N);

    for (int i = 0; i < N; ++i) {
      int I = i;
      new Thread(() -> {
        Long id = allWidgets.get(I).getId();
        repo.deleteWidget(id);
        assertEquals(0, repo.listWidgets().stream()
          .filter(w -> w.getId().equals(id))
          .count(), format("listWidgets should not contain deleted id %s", id));
        deletionFinished.countDown();
      }).start();
    }
    deletionFinished.await();

    var widgets = repo.listWidgets();
    assertThat(widgets).isEmpty();

  }

  private boolean compareWithQuery(Widget<?> widget, WidgetUpdateRequest query) {
    log.debug("comparing widget {} with query {}", widget, query);
    return Optional.ofNullable(query.getZ()).map(z -> z.equals(widget.getZ())).orElse(true) &&
      Optional.ofNullable(query.getX()).map(x -> x.equals(widget.getX())).orElse(true) &&
      Optional.ofNullable(query.getY()).map(y -> y.equals(widget.getY())).orElse(true) &&
      Optional.ofNullable(query.getZ()).map(height -> height.equals(widget.getHeight())).orElse(true) &&
      Optional.ofNullable(query.getWidth()).map(width -> width.equals(widget.getWidth())).orElse(true);
  }

  @Test
  @Timeout(value = 50, unit = TimeUnit.SECONDS)
  public void editAndDeleteWidgetsConcurrently() throws InterruptedException {
    final int N = 500;
    //region 1.create N widgets
    CountDownLatch start = new CountDownLatch(1);
    var creationFinished = createNSameWidgetsConcurrently(N, createCreationQuery(), start);
    start.countDown();
    creationFinished.await();
    List<Long> ids = repo.listWidgets()
      .stream()
      .map(Widget::getId)
      .collect(Collectors.toList());
    Collections.shuffle(ids);

    //endregion
    var random = new Random();
    // region 2. either edit them or delete them
    CountDownLatch startEditing = new CountDownLatch(1);
    CountDownLatch finishEditing = new CountDownLatch(N);

    ConcurrentSkipListSet<Long> deleted = new ConcurrentSkipListSet<>();
    for (int i = 0; i < N; ++i) {
      boolean isDeletion = random.nextBoolean();
      final int I = i;
      if (isDeletion) {
        new Thread(() -> {
          try {
            startEditing.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Long id = ids.get(I);
          repo.deleteWidget(id);
          deleted.add(id);
          finishEditing.countDown();

        }).start();
      } else {
        new Thread(() -> {
          try {
            startEditing.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          repo.updateWidget(ids.get(I), WidgetUpdateRequest.builder().z(400).build());
          finishEditing.countDown();
        }).start();
      }

    }


    //endregion

    // run the threads
    startEditing.countDown();
    finishEditing.await();

    //endregion


    //3. assertions
    List<Long> idsAfterDeletion = repo.listWidgets()
      .stream()
      .map(Widget::getId)
      .collect(Collectors.toUnmodifiableList());

    assertThat(idsAfterDeletion).doesNotContainAnyElementsOf(deleted);
    assertThat(idsAfterDeletion).hasSize(ids.size() - deleted.size());
    assertThat(idsAfterDeletion).containsAnyElementsOf(ids);

  }


  private CountDownLatch createNSameWidgetsConcurrently(int N, WidgetUpdateRequest query, CountDownLatch start) {
    CountDownLatch doneCreating = new CountDownLatch(N);
    for (int i = 0; i < N; ++i) {
      Thread thread = new Thread(() -> {
        try {
          start.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        repo.createWidget(query);
        doneCreating.countDown();
      });
      thread.start();
    }
    return doneCreating;
  }

}
