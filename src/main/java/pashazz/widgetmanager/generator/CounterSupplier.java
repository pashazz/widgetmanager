package pashazz.widgetmanager.generator;

import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class CounterSupplier implements Supplier<Long> {

  private long counter = 0;
  @Override
  public Long get() {
    return ++counter;
  }
}
