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
package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.queryservice.record.ResultDataQuery;
import eu.openanalytics.phaedra.queryservice.service.QueryService;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultDataDTO;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ResultDataQueryController {

  private final QueryService queryService;

  public ResultDataQueryController(QueryService queryService) {
    this.queryService = queryService;
  }

  @QueryMapping
  public List<ResultDataDTO> resultData(@Argument ResultDataQuery query) {
    return queryService.queryResultData(query);
  }
}
