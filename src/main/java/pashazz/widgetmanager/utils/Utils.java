package pashazz.widgetmanager.utils;

import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Utils {
  /**
   * @param binarySearchResult result of {@link java.util.Collections.binarySearch()}
   * @return an insertion position that Collections.binarySearch has found
   */
  static final LinkedHashMap<String, Function<Duration, Integer>>  DURATION_PARTS = new LinkedHashMap<>();
  static {
    DURATION_PARTS.put("d", d -> (int) d.toDaysPart());
    DURATION_PARTS.put("h", Duration::toHoursPart);
    DURATION_PARTS.put("m", Duration::toMinutesPart);
    DURATION_PARTS.put("s",  Duration::toSecondsPart);
    DURATION_PARTS.put("ms", Duration::toMillisPart);
    DURATION_PARTS.put("ns", Duration::toNanosPart);
  }


  public static int getInsertionPosition(int binarySearchResult) {
    if (binarySearchResult < 0) {
      return - (binarySearchResult + 1);
    }
    return binarySearchResult;

  }


  public static String printDuration (Duration dur) {

    StringBuilder sb = new StringBuilder();
    for (var converter : DURATION_PARTS.entrySet()) {
      int value = converter.getValue().apply(dur);
      if (value  == 0) {
        continue;
      }
      sb.append(value);
      sb.append(converter.getKey());
      sb.append(" ");
    }
    return sb.toString();

  }
    /**
   * This method will shift the iterator so that when next() is called, it'd point to the index.
   * @param iterator
   * @param index
   */
  public static <T> Iterator<T> shiftIteratorTo(Iterator<T> iterator, int index) {
    for (int i = 0; i < index; ++i) {
      if (iterator.hasNext()) {
        iterator.next();
      }
    }
    return iterator;
  }

}
