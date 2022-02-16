package pashazz.widgetmanager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pashazz.widgetmanager.entity.db.JPAWidgetBuilder;
import pashazz.widgetmanager.entity.db.JpaWidgetImpl;
import pashazz.widgetmanager.entity.interfaces.WidgetBuilder;
import pashazz.widgetmanager.entity.validator.CreationRequestValidator;
import pashazz.widgetmanager.entity.validator.RequestValidator;
import pashazz.widgetmanager.entity.validator.UpdateRequestValidator;
import pashazz.widgetmanager.factory.MutableWidgetFactory;
import pashazz.widgetmanager.generator.CounterSupplier;
import pashazz.widgetmanager.repository.WidgetRepository;
import pashazz.widgetmanager.repository.concurrent.ConcurrentWidgetRepository;
import pashazz.widgetmanager.repository.db.DbWidgetRepository;
import pashazz.widgetmanager.repository.db.jpa.JpaWidgetRepository;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

@Configuration
@Profile("db")
@EnableJpaRepositories("pashazz.widgetmanager.repository.db.jpa")
@EntityScan("pashazz.widgetmanager.entity.db")
@EnableAutoConfiguration
public class DBRepositoryConfiguration {

  @Autowired
  private JpaWidgetRepository jpaWidgetRepository;

  @Autowired
  private EntityManager entityManager;

  @Bean
  public WidgetRepository<Long> widgetRepository() {
    return new ConcurrentWidgetRepository<>(
      new DbWidgetRepository(
        jpaWidgetRepository,
        new MutableWidgetFactory<>(
          idGenerator(),
          creationValidator(),
          updateValidator(),
          builderSupplier()
        ),
        entityManager
      ));
  }

  private Supplier<WidgetBuilder<Long, JpaWidgetImpl>> builderSupplier() {
    return JPAWidgetBuilder::new;
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
