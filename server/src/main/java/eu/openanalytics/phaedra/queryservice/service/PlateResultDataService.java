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

import eu.openanalytics.phaedra.plateservice.dto.PlateDTO;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultSetDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlateResultDataService {

//  private final HttpGraphQlClient graphQlClient;

  public PlateResultDataService() {
//    WebClient client = WebClient.builder()
//        .baseUrl("https://phaedra.poc.openanalytics.io/phaedra/resultdata-service/graphql")
//        .build();
//    graphQlClient = HttpGraphQlClient.builder(client).build();
  }

  public Mono<List<ResultSetDTO>> getPlateResultSet(List<PlateDTO> plates) {
    // language=GraphQL
//    String document = """
//            query latestResultSetsByPlateIds($plateIds: [ID]) {
//                resultSets:latestResultSetsByPlateIds(plateIds: $plateIds) {
//                    id
//                    plateId
//                    protocolId
//                    measId
//                    outcome
//                }
//            }
//            """;
//    return graphQlClient.document(document)
//        .variable("plateIds", plates.stream().map(PlateDTO::getId).toArray())
//        .retrieve("resultSets")
//        .toEntityList(ResultSetDTO.class);
    return null;
  }
}
