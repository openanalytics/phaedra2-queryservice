package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.ProjectDTO;
import eu.openanalytics.phaedra.queryservice.dto.DateFilter;
import eu.openanalytics.phaedra.queryservice.dto.MetaDataFilter;
import eu.openanalytics.phaedra.queryservice.dto.ProjectFilter;
import eu.openanalytics.phaedra.queryservice.dto.StringFilter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
  public List<ProjectDTO> projects(@Argument ProjectFilter projectFilter) {
    List<ProjectDTO> result = plateServiceClient.getProjects();
    if (projectFilter != null) {
      if (projectFilter.getName() != null) {
        StringFilter filter = projectFilter.getName();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getName().startsWith(filter.startsWith))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getName().endsWith(filter.endsWith))
            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getName().contains(filter.contains))
            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getName().matches(filter.regex))
            .toList();
      }

      if (projectFilter.getCreatedBy() != null) {
        StringFilter filter = projectFilter.getCreatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getCreatedBy().startsWith(filter.startsWith))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getCreatedBy().endsWith(filter.endsWith))
            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getCreatedBy().contains(filter.contains))
            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getCreatedBy().matches(filter.regex))
            .toList();
      }

      if (projectFilter.getUpdatedBy() != null) {
        StringFilter filter = projectFilter.getUpdatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getUpdatedBy().startsWith(filter.startsWith))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getUpdatedBy().endsWith(filter.endsWith))
            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getUpdatedBy().contains(filter.contains))
            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getUpdatedBy().matches(filter.regex))
            .toList();
      }

      if (projectFilter.getCreatedOn() != null) {
        DateFilter filter = projectFilter.getCreatedOn();
        result = result.stream()
            .filter(p -> filter.before != null && p.getCreatedOn().before(filter.before))
            .filter(p -> filter.after != null && p.getCreatedOn().after(filter.after))
            .filter(p -> filter.on != null && p.getCreatedOn().equals(filter.on))
            .toList();
      }

      if (projectFilter.getUpdatedOn() != null) {
        DateFilter filter = projectFilter.getUpdatedOn();
        result = result.stream()
            .filter(p -> filter.before != null && p.getUpdatedOn().before(filter.before))
            .filter(p -> filter.after != null && p.getUpdatedOn().after(filter.after))
            .filter(p -> filter.on != null && p.getUpdatedOn().equals(filter.on))
            .toList();
      }

      if (projectFilter.getTags() != null) {
        MetaDataFilter filter = projectFilter.getTags();
        result = result.stream()
            .filter(p -> filter.containsTags != null
                && p.getTags().containsAll(filter.containsTags))
            .toList();
      }

      if (projectFilter.getProperties() != null) {
        MetaDataFilter filter = projectFilter.getProperties();
        result = result.stream()
            .filter(p -> filter.containsProperties != null
                && p.getProperties().stream().map(prop -> prop.propertyName()).toList()
                .containsAll(filter.containsProperties))
            .toList();
      }
    }
    return result;
  }
}
