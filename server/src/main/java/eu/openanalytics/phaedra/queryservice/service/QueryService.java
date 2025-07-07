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
package eu.openanalytics.phaedra.queryservice.service;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceGraphQLClient;
import eu.openanalytics.phaedra.plateservice.dto.PlateMeasurementDTO;
import eu.openanalytics.phaedra.queryservice.record.IdFilter;
import eu.openanalytics.phaedra.queryservice.record.ResultDataQuery;
import eu.openanalytics.phaedra.queryservice.record.ResultSetQuery;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceGraphQLClient;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultDataDTO;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultSetDTO;
import eu.openanalytics.phaedra.resultdataservice.enumeration.StatusCode;
import eu.openanalytics.phaedra.resultdataservice.record.ResultDataFilter;
import eu.openanalytics.phaedra.resultdataservice.record.ResultSetFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

  private final ResultDataServiceGraphQLClient resultDataServiceGraphQLClient;
  private final PlateServiceGraphQLClient plateServiceGraphQLClient;

  public QueryService(ResultDataServiceGraphQLClient resultDataServiceGraphQLClient,
      PlateServiceGraphQLClient plateServiceGraphQLClient) {
    this.resultDataServiceGraphQLClient = resultDataServiceGraphQLClient;
    this.plateServiceGraphQLClient = plateServiceGraphQLClient;
  }

  public List<ResultSetDTO> queryResultSets(ResultSetQuery query) {
    Set<Long> resultSetIds = extractIdsFromFilter(query.id());
    Set<Long> plateIds = extractIdsFromFilter(query.plateId());

    if (isTrue(query.activeMeasurementOnly())) {
      addActiveMeasurements(plateIds);
    }

    Set<Long> measurementIds = extractIdsFromFilter(query.measId());
    Set<Long> protocolIds = extractIdsFromFilter(query.protocolId());
    Set<StatusCode> status = new HashSet<>();

    if (query.outcome() != null) {
      status.add(query.outcome());
    }

    ResultSetFilter resultSetFilter = new ResultSetFilter(
        new ArrayList<>(resultSetIds),
        new ArrayList<>(plateIds),
        new ArrayList<>(measurementIds),
        new ArrayList<>(protocolIds),
        new ArrayList<>(status),
        query.mostRecentResultSetOnly());
    return resultDataServiceGraphQLClient.getResultSets(resultSetFilter);
  }

  public List<ResultDataDTO> queryResultData(ResultDataQuery query) {
    Set<Long> resultDataIds = new HashSet<>();
    Set<Long> resultSetIds = new HashSet<>();
    Set<Long> featureIds = new HashSet<>();
    Set<Long> protocolIds = new HashSet<>();

    ResultSetQuery resultSetQuery = ResultSetQuery.withPartialFilter(
        query.protocolId(),
        query.plateId(),
        query.measId(),
        query.activeMeasurementOnly(),
        query.mostRecentResultSetOnly());
    List<ResultSetDTO> resultSets = queryResultSets(resultSetQuery);
    if (isEmpty(resultSets)) {
      resultSetIds.addAll(resultSets.stream().map(ResultSetDTO::getId).toList());
    }

    if (query.id() != null) {
      if (query.id().equals() != null) {
        resultDataIds.add(query.id().equals());
      }
      if (isNotEmpty(query.id().in())) {
        resultDataIds.addAll(query.id().in());
      }
    }

    if (query.resultSetId() != null) {
      if (query.resultSetId().equals() != null) {
        resultSetIds.add(query.resultSetId().equals());
      }
      if (isNotEmpty(query.resultSetId().in())) {
        resultSetIds.addAll(query.resultSetId().in());
      }
    }

    if (query.featureId() != null) {
      if (query.featureId().equals() != null) {
        featureIds.add(query.featureId().equals());
      }
      if (isNotEmpty(query.featureId().in())) {
        featureIds.addAll(query.featureId().in());
      }
    }

    if (query.protocolId() != null) {
      if (query.protocolId().equals() != null) {
        protocolIds.add(query.protocolId().equals());
      }
      if (isNotEmpty(query.protocolId().in())) {
        protocolIds.addAll(query.protocolId().in());
      }
    }

    ResultDataFilter resultDataFilter = new ResultDataFilter(
        new ArrayList<>(resultDataIds),
        new ArrayList<>(resultSetIds),
        new ArrayList<>(protocolIds),
        new ArrayList<>(featureIds)
    );
    return resultDataServiceGraphQLClient.getResultData(resultDataFilter);
  }

  private Set<Long> extractIdsFromFilter(IdFilter filter) {
    Set<Long> ids = new HashSet<>();
    if (filter != null) {
      if (filter.equals() != null) {
        ids.add(filter.equals());
      }
      if (filter.in() != null) {
        ids.addAll(filter.in());
      }
    }
    return ids;
  }

  private void addActiveMeasurements(Set<Long> plateIds) {
    List<PlateMeasurementDTO> activeMeasurements = plateServiceGraphQLClient
        .getActivePlateMeasurements(new ArrayList<>(plateIds));
    plateIds.addAll(activeMeasurements.stream()
        .map(PlateMeasurementDTO::getMeasurementId)
        .toList());
  }
}
