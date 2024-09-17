package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.ExperimentDTO;
import eu.openanalytics.phaedra.queryservice.record.ExperimentFilter;
import eu.openanalytics.phaedra.queryservice.record.IdFilter;
import eu.openanalytics.phaedra.queryservice.utils.FilterUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ExperimentQueryController {

  private final PlateServiceClient plateServiceClient;

  public ExperimentQueryController(PlateServiceClient plateServiceClient) {
    this.plateServiceClient = plateServiceClient;
  }

  @QueryMapping
  public List<ExperimentDTO> experiments(@Argument(name = "filter") ExperimentFilter experimentFilter) {
    List<ExperimentDTO> results = new ArrayList<>();
    if (experimentFilter != null) {
      if (experimentFilter.projectId() != null) {
        IdFilter filter = experimentFilter.projectId();
        if (filter.equals() != null)
          results.addAll(plateServiceClient.getExperiments(filter.equals()));
        else if (CollectionUtils.isNotEmpty(filter.in())) {
          for (Long projectId: filter.in())
            results.addAll(plateServiceClient.getExperiments(projectId));
        } else {
          results.addAll(plateServiceClient.getExperiments());
        }
      }

      if (experimentFilter.name() != null) {
        results = FilterUtils.filterByString(results, experimentFilter.name(), ExperimentDTO::getName);
      }
      if (experimentFilter.createdBy() != null) {
        results = FilterUtils.filterByString(results, experimentFilter.createdBy(), ExperimentDTO::getCreatedBy);
      }
      if (experimentFilter.updatedBy() != null) {
        results = FilterUtils.filterByString(results, experimentFilter.updatedBy(), ExperimentDTO::getUpdatedBy);
      }
      if (experimentFilter.createdOn() != null) {
        results = FilterUtils.filterByDate(results, experimentFilter.createdOn(), ExperimentDTO::getCreatedOn);
      }
      if (experimentFilter.updatedOn() != null) {
        results = FilterUtils.filterByDate(results, experimentFilter.updatedOn(), ExperimentDTO::getUpdatedOn);
      }
      if (experimentFilter.tags() != null) {
        results = FilterUtils.filterByMetaData(results, experimentFilter.tags(), ExperimentDTO::getTags);
      }
      if (experimentFilter.properties() != null) {
        results = FilterUtils.filterByMetaData(results, experimentFilter.properties(),
            e -> e.getProperties().stream().map(prop -> prop.propertyName()).toList());
      }

    }
    return results;
  }
}
