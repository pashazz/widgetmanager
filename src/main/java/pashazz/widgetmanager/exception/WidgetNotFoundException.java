package pashazz.widgetmanager.exception;

public class WidgetNotFoundException extends RuntimeException {
  public WidgetNotFoundException(String id) {
    super(String.format("Widget not found by id: %s", id));
  }
}
