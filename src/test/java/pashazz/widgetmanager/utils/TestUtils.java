package pashazz.widgetmanager.utils;

import pashazz.widgetmanager.entity.query.WidgetUpdateRequest;

public class TestUtils {
    public static WidgetUpdateRequest createStaticCreationQuery(Integer zOrder) {
    return new WidgetUpdateRequest(30, 20, 100, 200, zOrder);
  }
}
