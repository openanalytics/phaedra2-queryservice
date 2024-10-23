/**
 * Phaedra II
 *
 * Copyright (C) 2016-2024 Open Analytics
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
package eu.openanalytics.phaedra.queryservice.record;

import eu.openanalytics.phaedra.resultdataservice.enumeration.StatusCode;

public record ResultSetQuery (
    IdFilter id,
    IdFilter protocolId,
    IdFilter plateId,
    IdFilter measId,
    DateFilter executionStartTimeStamp,
    DateFilter executionEndTimeStamp,
    StatusCode outcome,
    Boolean activeMeasurementOnly,
    Boolean mostRecentResultSetOnly
) {

  public static ResultSetQuery withPartialFilter(
      IdFilter protocolId,
      IdFilter plateId,
      IdFilter measId,
      Boolean activeMeasurementOnly,
      Boolean mostRecentResultSetOnly) {

    return new ResultSetQuery(
        null, protocolId, plateId, measId,
        null, null, null,
        activeMeasurementOnly, mostRecentResultSetOnly);
  }
}
