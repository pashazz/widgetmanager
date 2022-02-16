package pashazz.widgetmanager.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pashazz.widgetmanager.entity.interfaces.Widget;
import pashazz.widgetmanager.entity.interfaces.WidgetBuilder;
import pashazz.widgetmanager.entity.memory.InMemoryLongWidgetBuilder;
import pashazz.widgetmanager.entity.validator.CreationRequestValidator;
import pashazz.widgetmanager.entity.validator.RequestValidator;
import pashazz.widgetmanager.entity.validator.UpdateRequestValidator;
import pashazz.widgetmanager.factory.StandardWidgetFactory;
import pashazz.widgetmanager.generator.CounterSupplier;
import pashazz.widgetmanager.repository.WidgetRepository;
import pashazz.widgetmanager.repository.concurrent.ConcurrentWidgetRepository;
import pashazz.widgetmanager.repository.memory.InMemoryWidgetRepository;

import java.util.function.Supplier;

@Configuration
// This disables database integration in Spring Boot while enabling other autoconfigurations
@EnableAutoConfiguration(exclude = {
  DataSourceAutoConfiguration.class,
  DataSourceTransactionManagerAutoConfiguration.class,
  HibernateJpaAutoConfiguration.class})
@Profile("memory")
public class InMemoryRepositoryConfiguration {

  @Bean
  public WidgetRepository<Long> widgetRepository() {
    return new ConcurrentWidgetRepository(
      new InMemoryWidgetRepository(
        new StandardWidgetFactory<Long, Widget<Long>>(idGenerator(),
          creationValidator(),
          updateValidator(),
          builderSupplier()
        )
      ));
  }

  private Supplier<WidgetBuilder<Long, Widget<Long>>> builderSupplier() {
    return InMemoryLongWidgetBuilder::new;
  }

  private RequestValidator updateValidator() {
    return new UpdateRequestValidator();
  }

  private Supplier<Long> idGenerator() {
    return new CounterSupplier();
  }

  private RequestValidator creationValidator() {
    return new CreationRequestValidator();
  }

}
