package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.client.PlateServiceGraphQLClient;
import eu.openanalytics.phaedra.plateservice.dto.WellDTO;
import eu.openanalytics.phaedra.queryservice.record.WellFilter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WellQueryController {


  private final PlateServiceGraphQLClient plateServiceGraphQLClient;

  public WellQueryController(PlateServiceGraphQLClient plateServiceGraphQLClient) {
    this.plateServiceGraphQLClient = plateServiceGraphQLClient;
  }

  @QueryMapping
  public List<WellDTO> wells(@Argument(name = "filter") WellFilter wellFilter) {
    List<WellDTO> results = new ArrayList<>();

    if (wellFilter != null) {
      if (wellFilter.id() != null) {
        if (wellFilter.id().equals() != null) {
          results.add(plateServiceGraphQLClient.getWell(wellFilter.id().equals()));
        } else if (wellFilter.id().in() != null) {
          results.addAll(plateServiceGraphQLClient.getWells(wellFilter.id().in()));
        }
      }

      if (wellFilter.plateId() != null) {
        if (wellFilter.plateId().equals() != null) {
          results.addAll(plateServiceGraphQLClient.getWellsByPlateId(wellFilter.plateId().equals()));
        } else if (wellFilter.id().in() != null) {
          results.addAll(plateServiceGraphQLClient.getWellsByPlateIds(wellFilter.plateId().in()));
        }
      }

      if (wellFilter.experimentId() != null) {
        if (wellFilter.experimentId().equals() != null) {
          results.addAll(plateServiceGraphQLClient.getWellsByExperimentId(wellFilter.experimentId().equals()));
        } else if (wellFilter.id().in() != null) {
          results.addAll(plateServiceGraphQLClient.getWellsByExperimentIds(wellFilter.experimentId().in()));
        }
      }
    }

    return results;
  }

}
