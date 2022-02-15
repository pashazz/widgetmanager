package pashazz.widgetmanager.rest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import pashazz.widgetmanager.WidgetmanagerApplication;
import pashazz.widgetmanager.rest.entity.TestWidget;
import pashazz.widgetmanager.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  classes = WidgetmanagerApplication.class)
@ActiveProfiles("db")
@Slf4j
public class RestApiTest {


  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void shouldAddWidgetAndListItThenRemoveAndReturnEmptyList() {
    TestWidget widget1 = restTemplate.postForObject(getUrl("/widgets"), TestUtils.createStaticCreationQuery(1), TestWidget.class);

    // 1. assert that widget in the list
    List<HashMap<String, Object>> allWidgets = restTemplate.getForObject("/widgets/all", List.class);
    assertThat(allWidgets).hasSize(1);
    assertWidget(allWidgets.get(0), widget1);

    //2. perform deletion
    restTemplate.delete(getUrl("/widgets/" + widget1.getId()));

    //3. get list and assert empty
    List<HashMap<String, Object>> emptyList = restTemplate.getForObject("/widgets/all", List.class);
    assertThat(emptyList).isEmpty();

  }

  private void assertWidget(Map<String, Object> m, TestWidget expected) {
    assertThat(m).containsKeys("id", "x", "y", "z", "width", "height", "lastUpdatedAt");
    assertThat(m.get("id")).isEqualTo(expected.getId().intValue());
    assertThat(m.get("x")).isEqualTo(expected.getX());
    assertThat(m.get("y")).isEqualTo(expected.getY());
    assertThat(m.get("z")).isEqualTo(expected.getZ());
    assertThat(m.get("width")).isEqualTo(expected.getWidth());
    assertThat(m.get("height")).isEqualTo(expected.getHeight());
    assertThat(LocalDateTime.parse(m.get("lastUpdatedAt").toString()))
      .isEqualTo(expected.getLastUpdatedAt().toString());
  }

  private String getUrl(String path) {
    return format("http://localhost:%s%s", port, path);
  }
}
