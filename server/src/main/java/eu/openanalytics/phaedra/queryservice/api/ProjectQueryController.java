package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.ProjectDTO;
import eu.openanalytics.phaedra.queryservice.record.ProjectFilter;
import eu.openanalytics.phaedra.queryservice.utils.FilterUtils;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectQueryController {

  private final PlateServiceClient plateServiceClient;

  public ProjectQueryController(PlateServiceClient plateServiceClient) {
    this.plateServiceClient = plateServiceClient;
  }

  @QueryMapping
  public List<ProjectDTO> projects(@Argument(name = "filter") ProjectFilter projectFilter) {
    List<ProjectDTO> result = plateServiceClient.getProjects();
    if (projectFilter != null) {
      if (projectFilter.name() != null) {
        result = FilterUtils.filterByString(result, projectFilter.name(), ProjectDTO::getName);
      }
      if (projectFilter.createdBy() != null) {
        result = FilterUtils.filterByString(result, projectFilter.createdBy(), ProjectDTO::getCreatedBy);
      }
      if (projectFilter.updatedBy() != null) {
        result = FilterUtils.filterByString(result, projectFilter.updatedBy(), ProjectDTO::getUpdatedBy);
      }
      if (projectFilter.createdOn() != null) {
        result = FilterUtils.filterByDate(result, projectFilter.createdOn(), ProjectDTO::getCreatedOn);
      }
      if (projectFilter.updatedOn() != null) {
        result = FilterUtils.filterByDate(result, projectFilter.updatedOn(), ProjectDTO::getUpdatedOn);
      }
      if (projectFilter.tags() != null) {
        result = FilterUtils.filterByMetaData(result, projectFilter.tags(), ProjectDTO::getTags);
      }
      if (projectFilter.properties() != null) {
        result = FilterUtils.filterByMetaData(result, projectFilter.properties(),
            p -> p.getProperties().stream().map(prop -> prop.propertyName()).toList());
      }
    }
    return result;
  }
}
