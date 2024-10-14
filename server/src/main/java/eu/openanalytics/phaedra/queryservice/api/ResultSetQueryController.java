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
import eu.openanalytics.phaedra.resultdataservice.enumeration.StatusCode;
import java.util.ArrayList;
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
      List<Long> resultSetIds = new ArrayList<>();
      List<Long> plateIds = new ArrayList<>();
      List<Long> measurementIds = new ArrayList<>();
      List<Long> protocolIds = new ArrayList<>();
      List<StatusCode> status = new ArrayList<>();

      if (filter.id() != null) {
        if (filter.id().equals() != null) {
          resultSetIds.add(filter.id().equals());
        }
        if (filter.id().in() != null) {
          resultSetIds.addAll(filter.id().in());
        }
      }

      if (filter.plateId() != null) {
        if (filter.plateId().equals() != null) {
          plateIds.add(filter.plateId().equals());
        }
        if (filter.plateId().in() != null) {
          plateIds.addAll(filter.plateId().in());
        }
      }

      if (filter.measId() != null) {
        if (filter.measId().equals() != null) {
          measurementIds.add(filter.measId().equals());
        }
        if (filter.measId().in() != null) {
          measurementIds.addAll(filter.measId().in());
        }
      }

      if (filter.protocolId() != null) {
        if (filter.protocolId().equals() != null) {
          protocolIds.add(filter.protocolId().equals());
        }
        if (filter.protocolId().in() != null) {
          protocolIds.addAll(filter.protocolId().in());
        }
      }

      if (filter.outcome() != null) {
        status.add(filter.outcome());
      }

      return resultDataServiceGraphQLClient.getResultSets(
          new eu.openanalytics.phaedra.resultdataservice.record.ResultSetFilter(
              resultSetIds, plateIds, measurementIds, protocolIds, status
          )
      );
    }
    return null;
  }
}
