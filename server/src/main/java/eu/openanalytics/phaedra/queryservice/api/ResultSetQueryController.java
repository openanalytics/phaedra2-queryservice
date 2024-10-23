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

import eu.openanalytics.phaedra.queryservice.record.ResultSetQuery;
import eu.openanalytics.phaedra.queryservice.service.QueryService;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultSetDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ResultSetQueryController {

  private final QueryService queryService;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public ResultSetQueryController(QueryService queryService) {
    this.queryService = queryService;
  }

  @QueryMapping
  public List<ResultSetDTO> resultSets(@Argument ResultSetQuery query) {
    if (query != null) {
      return queryService.queryResultSets(query);
    }
    return List.of();
  }
}
