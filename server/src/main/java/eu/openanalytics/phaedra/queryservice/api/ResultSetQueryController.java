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
package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.queryservice.record.ResultSetFilter;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceGraphQLClient;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultSetDTO;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ResultSetQueryController {

  private final ResultDataServiceGraphQLClient resultDataServiceGraphQLClient;

  public ResultSetQueryController(ResultDataServiceGraphQLClient resultDataServiceGraphQLClient) {
    this.resultDataServiceGraphQLClient = resultDataServiceGraphQLClient;
  }

  @QueryMapping
  public List<ResultSetDTO> resultSets(@Argument ResultSetFilter filter) {
    if (filter != null) {
      if (filter.id() != null) {
        if (filter.id().equals() != null) {
          return List.of(resultDataServiceGraphQLClient.getResultSet(filter.id().equals()));
        }
      }
      if (filter.plateId() != null) {
        if (filter.plateId().equals() != null) {
          return resultDataServiceGraphQLClient.getResultSetsByPlateId(filter.plateId().equals());
        }
        if (filter.plateId().in() != null) {
          return resultDataServiceGraphQLClient.getResultSetsByPlateIds(filter.plateId().in());
        }
      }
      if (filter.measId() != null) {
        if (filter.measId().equals() != null) {
          return resultDataServiceGraphQLClient.getResultSetsByMeasurementId(filter.measId().equals());
        }
        if (filter.measId().in() != null) {
          return resultDataServiceGraphQLClient.getResultSetsByMeasurementIds(filter.measId().in());
        }
      }
      if (filter.protocolId() != null) {
        if (filter.protocolId().equals() != null) {
          return resultDataServiceGraphQLClient.getResultSetsByProtocolId(filter.protocolId().equals());
        }
        if (filter.protocolId().in() != null) {
          return resultDataServiceGraphQLClient.getResultSetsByProtocolIds(filter.protocolId().in());
        }
      }
//      TODO: Is this necessary? Find use-cases for it!
//      if (filter.executionStartTimeStamp() != null) {
//
//      }
//      if (filter.executionEndTimeStamp() != null) {
//
//      }
      if (filter.outcome() != null) {

      }
    }
    return null;
  }
}
