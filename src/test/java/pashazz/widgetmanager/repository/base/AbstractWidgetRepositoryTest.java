package pashazz.widgetmanager.repository.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import pashazz.widgetmanager.WidgetmanagerApplication;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.exception.WidgetCreationException;
import pashazz.widgetmanager.exception.WidgetNotFoundException;
import pashazz.widgetmanager.repository.WidgetRepository;
import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pashazz.widgetmanager.utils.TestUtils.createStaticCreationQuery;

@SpringBootTest(classes = WidgetmanagerApplication.class)
@ComponentScan
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public abstract class AbstractWidgetRepositoryTest {

  @Autowired
  private WidgetRepository<Long> repo;


  @Test
  void shouldReturnWidgetsInSortedOrderWhenCreated() {
    // region init
    var widget1 = repo.createWidget(createStaticCreationQuery(100));

    var widget2 = repo.createWidget(createStaticCreationQuery(-5));

    var widget3 = repo.createWidget(createStaticCreationQuery(20));
    // list widgets 1
    var allWidgets = repo.listWidgets();
    //endregion

    //region assert
    assertThat(allWidgets)
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactly(widget2, widget3, widget1);
    //endregion

    //region change the zOrder of widget 2 from -5 to 25
    var newWidget2 = repo.updateWidget(widget2.getId(),
      WidgetUpdateRequest.builder()
        .z(25)
        .build());
    //endregion

    //region assert
    assertThat(newWidget2.getZ()).isEqualTo(25);
    assertThat(newWidget2.getLastUpdatedAt()).isAfter(widget2.getLastUpdatedAt());

    List<Widget<Long>> newAllWidgets = repo.listWidgets();

    assertThat(newAllWidgets)
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactly(widget3, newWidget2, widget1);
    //endregion
  }

  /**
   * Test that widgets are correctly inserted in the foreground
   */
  @Test
  public void shouldCorrectlyInsertAtTheForeground() {
    // region init
    var widget1 = repo.createWidget(createStaticCreationQuery(100));

    var widget2 = repo.createWidget(createStaticCreationQuery(-5));

    var widget3 = repo.createWidget(createStaticCreationQuery(20));

    var widgetDefault = repo.createWidget(createStaticCreationQuery(null));

    var widgets = repo.listWidgets();
    assertEquals(4, widgets.size());
    var array = new ArrayList<>(widgets);
    assertEquals(101, array.get(array.size() - 1).getZ());

  }

  @Test
  public void shouldShiftZOrderSoThatZOrderIsUnique() {

    //region init
    for (int i = 0; i < 5; ++i) {
      repo.createWidget(createStaticCreationQuery(1));
    }
    //endregion

    var widgets = new ArrayList<>(repo.listWidgets());
    for (int i = 0; i < 5; ++i) {
      assertEquals(i + 1, widgets.get(i).getZ());
    }
  }

  @Test
  public void shouldReturnCorrectOrderInPagesIfPaginationIsUsed() {
    final int ELEM_SIZE = 9000;
    Random random = new Random();

    int min = Integer.MAX_VALUE;
    for (int i = 0; i < ELEM_SIZE; ++i) {
      Integer zOrder = random.nextInt(ELEM_SIZE * 2);
      min = Math.min(min, zOrder);
      repo.createWidget(createStaticCreationQuery(zOrder));
    }
    for (int i = 0; i < 10; ++i) {

      var page = repo.listWidgets(i, ELEM_SIZE / 10);
      var sorted = new ArrayList<>(page);
      Collections.sort(sorted);
      log.trace("page: {}", page);
      log.trace("sorted: {}", sorted);
      assertThat(page).containsExactlyElementsOf(sorted);

      assertThat(sorted.get(0).getZ()).isGreaterThanOrEqualTo(min);
      min = sorted.get(sorted.size() - 1).getZ();
    }


  }

  @Test
  void shouldDeleteItemsCorrectly() {
    // region init
    var widget1 = repo.createWidget(createStaticCreationQuery(100));

    var widget2 = repo.createWidget(createStaticCreationQuery(-5));

    var widget3 = repo.createWidget(createStaticCreationQuery(20));
    // list widgets 1
    var allWidgets = repo.listWidgets();
    //endregion

    assertThat(allWidgets)
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactly(widget2, widget3, widget1);

    //region execute
    repo.deleteWidget(widget3.getId());
    //endregion
    var widgets = repo.listWidgets();
    assertThat(widgets)
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactly(widget2, widget1);

    assertThrows(WidgetNotFoundException.class, () -> repo.getWidget(widget3.getId()));
  }

  //region tests on error handling
  @Test
  void shouldNotCreateItemWithNullValues() {
    assertThrows(WidgetCreationException.class, () -> repo.createWidget(WidgetUpdateRequest.builder().build()));

    assertThrows(WidgetCreationException.class, () -> repo.createWidget(WidgetUpdateRequest.builder()
      .x(10)
      .build()));
    assertThrows(WidgetCreationException.class, () -> repo.createWidget(WidgetUpdateRequest.builder()
      .x(10)
      .y(-13)
      .build()));

  }

  @Test
  void shouldThrowIfGetByWrongId() {
    assertThrows(WidgetNotFoundException.class, () -> repo.getWidget(-3L));
  }

  //endregion


}
