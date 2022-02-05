package pashazz.widgetmanager.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair <F, S> {
  private F first;
  private S second;

}
