package eu.openanalytics.phaedra.queryservice.utils;

import eu.openanalytics.phaedra.queryservice.record.DateFilter;
import eu.openanalytics.phaedra.queryservice.record.MetaDataFilter;
import eu.openanalytics.phaedra.queryservice.record.StringFilter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class FilterUtils {

  public static <T> List<T> filterByString(List<T> result, StringFilter stringFilter,
      Function<T, String> getStringFunction) {
    return result.stream()
        .filter(e -> StringUtils.isBlank(stringFilter.startsWith())
            || getStringFunction.apply(e).startsWith(stringFilter.startsWith()))
        .filter(e -> StringUtils.isBlank(stringFilter.endsWith())
            || getStringFunction.apply(e).endsWith(stringFilter.endsWith()))
        .filter(e -> StringUtils.isBlank(stringFilter.contains())
            || getStringFunction.apply(e).contains(stringFilter.contains()))
        .filter(e -> StringUtils.isBlank(stringFilter.regex())
            || getStringFunction.apply(e).matches(stringFilter.regex()))
        .toList();
  }

  public static <T> List<T> filterByDate(List<T> result, DateFilter dateFilter,
      Function<T, Date> getDateFunction) {
    return result.stream()
        .filter(e -> dateFilter.before() == null || getDateFunction.apply(e).before(dateFilter.before()))
        .filter(e -> dateFilter.after() == null || getDateFunction.apply(e).after(dateFilter.after()))
        .filter(e -> dateFilter.on() == null || getDateFunction.apply(e).equals(dateFilter.on()))
        .toList();
  }

  public static <T> List<T> filterByMetaData(List<T> result, MetaDataFilter metaDataFilter,
      Function<T, List<String>> getListStringFunction) {
    return result.stream()
        .filter(p -> CollectionUtils.isEmpty(metaDataFilter.includes())
            || getListStringFunction.apply(p).containsAll(metaDataFilter.includes()))
        .toList();
  }
}
