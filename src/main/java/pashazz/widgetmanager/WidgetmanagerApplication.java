package pashazz.widgetmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class WidgetmanagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(WidgetmanagerApplication.class, args);
  }

}
