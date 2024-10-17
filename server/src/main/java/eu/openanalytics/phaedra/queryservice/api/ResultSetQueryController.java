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

import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceGraphQLClient;
import eu.openanalytics.phaedra.plateservice.dto.PlateMeasurementDTO;
import eu.openanalytics.phaedra.queryservice.record.ResultSetQuery;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceGraphQLClient;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultSetDTO;
import eu.openanalytics.phaedra.resultdataservice.enumeration.StatusCode;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ResultSetQueryController {

  private final ResultDataServiceGraphQLClient resultDataServiceGraphQLClient;
  private final PlateServiceGraphQLClient plateServiceGraphQLClient;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public ResultSetQueryController(ResultDataServiceGraphQLClient resultDataServiceGraphQLClient,
      PlateServiceGraphQLClient plateServiceGraphQLClient) {
    this.resultDataServiceGraphQLClient = resultDataServiceGraphQLClient;
    this.plateServiceGraphQLClient = plateServiceGraphQLClient;
  }

  @QueryMapping
  public List<ResultSetDTO> resultSets(@Argument ResultSetQuery query) {
    if (query != null) {
      logger.info("Query is not null");
      List<Long> resultSetIds = new ArrayList<>();
      List<Long> plateIds = new ArrayList<>();
      List<Long> measurementIds = new ArrayList<>();
      List<Long> protocolIds = new ArrayList<>();
      List<StatusCode> status = new ArrayList<>();

      if (query.id() != null) {
        if (query.id().equals() != null) {
          resultSetIds.add(query.id().equals());
        }
        if (query.id().in() != null) {
          resultSetIds.addAll(query.id().in());
        }
      }

      if (query.plateId() != null) {
        logger.info("Query.plateId is not null");
        if (query.plateId().equals() != null) {
          plateIds.add(query.plateId().equals());
        }
        if (query.plateId().in() != null) {
          plateIds.addAll(query.plateId().in());
        }
        if (isTrue(query.activeMeasOnly())) {
          logger.info("Query.activeMeasOnly is true");
          List<PlateMeasurementDTO> activeMeasurements = plateServiceGraphQLClient
              .getActivePlateMeasurements(plateIds);
          measurementIds.addAll(activeMeasurements.stream()
              .map(m -> m.getMeasurementId()).toList());
        }
      }

      if (query.protocolId() != null) {
        logger.info("Query.protocolId is not null");
        if (query.protocolId().equals() != null) {
          protocolIds.add(query.protocolId().equals());
        }
        if (query.protocolId().in() != null) {
          protocolIds.addAll(query.protocolId().in());
        }
      }

      if (query.measId() != null) {
        logger.info("Query.measId is not null");
        if (query.measId().equals() != null) {
          measurementIds.add(query.measId().equals());
        }
        if (query.measId().in() != null) {
          measurementIds.addAll(query.measId().in());
        }
      }

      if (query.outcome() != null) {
        logger.info("Query.outcome is not null");
        status.add(query.outcome());
      }

      return resultDataServiceGraphQLClient.getResultSets(
          new eu.openanalytics.phaedra.resultdataservice.record.ResultSetFilter(
              resultSetIds, plateIds, measurementIds, protocolIds, status
          )
      );
    }
    logger.info("Query is null");
    return null;
  }
}
