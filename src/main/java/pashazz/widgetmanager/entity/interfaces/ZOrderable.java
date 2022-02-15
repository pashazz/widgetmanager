package pashazz.widgetmanager.entity.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * This interface implies that the classes derived from it may be sorted by their Z-order
 * Used by in-memory implementation.
 */
public interface ZOrderable extends Comparable<ZOrderable> {
  int getZ();

  @Override
  default int compareTo(@NotNull ZOrderable o) {
    return Integer.compare(getZ(), o.getZ());
  }
}
