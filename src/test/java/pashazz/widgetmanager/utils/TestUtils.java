package pashazz.widgetmanager.utils;

import pashazz.widgetmanager.rest.request.WidgetUpdateRequest;

public class TestUtils {
  public static WidgetUpdateRequest createStaticCreationQuery(Integer zOrder) {
    return new WidgetUpdateRequest(30, 20, 100, 200, zOrder);
  }
}
