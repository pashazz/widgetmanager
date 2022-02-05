package pashazz.widgetmanager.entity;

import org.jetbrains.annotations.NotNull;


public interface ZOrderable extends Comparable<ZOrderable> {
  int getZ();


  @Override
  default int compareTo(@NotNull ZOrderable o) {
    return Integer.compare(getZ(), o.getZ());
  }
}
