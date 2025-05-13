/**
 * Phaedra II
 *
 * Copyright (C) 2016-2025 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.phaedra.queryservice.utils;

import eu.openanalytics.phaedra.queryservice.record.DateFilter;
import eu.openanalytics.phaedra.queryservice.record.MetaDataFilter;
import eu.openanalytics.phaedra.queryservice.record.StringFilter;

import java.util.HashSet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class FilterUtils {

  public static <T> List<T> filterByString(List<T> result, StringFilter stringFilter,
      Function<T, String> getStringFunction) {
    return result.stream()
        .filter(e -> StringUtils.isBlank(stringFilter.equals())
            || StringUtils.equals(getStringFunction.apply(e), stringFilter.equals()))
        .filter(e -> StringUtils.isBlank(stringFilter.startsWith())
            || StringUtils.startsWith(getStringFunction.apply(e), stringFilter.startsWith()))
        .filter(e -> StringUtils.isBlank(stringFilter.endsWith())
            || StringUtils.endsWith(getStringFunction.apply(e), stringFilter.endsWith()))
        .filter(e -> StringUtils.isBlank(stringFilter.contains())
            || StringUtils.contains(getStringFunction.apply(e), stringFilter.contains()))
        .filter(e -> StringUtils.isBlank(stringFilter.regex())
            || (StringUtils.isNotBlank(getStringFunction.apply(e))
            && getStringFunction.apply(e).matches(stringFilter.regex())))
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
            || new HashSet<>(getListStringFunction.apply(p)).containsAll(metaDataFilter.includes()))
        .toList();
  }
}
