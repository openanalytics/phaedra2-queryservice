package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.ProjectDTO;
import eu.openanalytics.phaedra.queryservice.record.DateFilter;
import eu.openanalytics.phaedra.queryservice.record.MetaDataFilter;
import eu.openanalytics.phaedra.queryservice.record.ProjectFilter;
import eu.openanalytics.phaedra.queryservice.record.StringFilter;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectQueryController {

  private final PlateServiceClient plateServiceClient;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public ProjectQueryController(PlateServiceClient plateServiceClient) {
    this.plateServiceClient = plateServiceClient;
  }

  @QueryMapping
  public List<ProjectDTO> projects(@Argument(name = "filter") ProjectFilter projectFilter) {
    List<ProjectDTO> result = plateServiceClient.getProjects();
    if (projectFilter != null) {
      if (projectFilter.name() != null) {
        StringFilter nameFilter = projectFilter.name();
        result = result.stream()
            .filter(p -> StringUtils.isBlank(nameFilter.startsWith()) || p.getName().startsWith(nameFilter.startsWith()))
            .filter(p -> StringUtils.isBlank(nameFilter.endsWith()) || p.getName().endsWith(nameFilter.endsWith()))
            .filter(p -> StringUtils.isBlank(nameFilter.contains()) || p.getName().contains(nameFilter.contains()))
            .filter(p -> StringUtils.isBlank(nameFilter.regex()) || p.getName().matches(nameFilter.regex()))
            .toList();
      }

      if (projectFilter.createdBy() != null) {
        StringFilter createdByFilter = projectFilter.createdBy();
        result = result.stream()
            .filter(p -> StringUtils.isBlank(createdByFilter.startsWith()) || p.getCreatedBy().startsWith(createdByFilter.startsWith()))
            .filter(p -> StringUtils.isBlank(createdByFilter.endsWith()) || p.getCreatedBy().endsWith(createdByFilter.endsWith()))
            .filter(p -> StringUtils.isBlank(createdByFilter.contains()) || p.getCreatedBy().contains(createdByFilter.contains()))
            .filter(p -> StringUtils.isBlank(createdByFilter.regex()) || p.getCreatedBy().matches(createdByFilter.regex()))
            .toList();
      }

      if (projectFilter.updatedBy() != null) {
        StringFilter updatedByFilter = projectFilter.updatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isBlank(updatedByFilter.startsWith()) || p.getUpdatedBy().startsWith(updatedByFilter.startsWith()))
            .filter(p -> StringUtils.isBlank(updatedByFilter.endsWith()) || p.getUpdatedBy().endsWith(updatedByFilter.endsWith()))
            .filter(p -> StringUtils.isBlank(updatedByFilter.contains()) | p.getUpdatedBy().contains(updatedByFilter.contains()))
            .filter(p -> StringUtils.isBlank(updatedByFilter.regex()) || p.getUpdatedBy().matches(updatedByFilter.regex()))
            .toList();
      }

      if (projectFilter.createdOn() != null) {
        DateFilter createdOnFilter = projectFilter.createdOn();
        result = result.stream()
            .filter(p -> createdOnFilter.before() == null || p.getCreatedOn().before(createdOnFilter.before()))
            .filter(p -> createdOnFilter.after() == null || p.getCreatedOn().after(createdOnFilter.after()))
            .filter(p -> createdOnFilter.on() == null | p.getCreatedOn().equals(createdOnFilter.on()))
            .toList();
      }

      if (projectFilter.updatedOn() != null) {
        DateFilter updatedOnFilter = projectFilter.updatedOn();
        result = result.stream()
            .filter(p -> updatedOnFilter.before() == null || p.getUpdatedOn().before(updatedOnFilter.before()))
            .filter(p -> updatedOnFilter.after() == null || p.getUpdatedOn().after(updatedOnFilter.after()))
            .filter(p -> updatedOnFilter.on() == null || p.getUpdatedOn().equals(updatedOnFilter.on()))
            .toList();
      }

      if (projectFilter.tags() != null) {
        MetaDataFilter tagFilter = projectFilter.tags();
        result = result.stream()
            .filter(p -> CollectionUtils.isEmpty(tagFilter.includes())
                || p.getTags().containsAll(tagFilter.includes()))
            .toList();
      }

      if (projectFilter.properties() != null) {
        MetaDataFilter propertiesFilter = projectFilter.properties();
        result = result.stream()
            .filter(p -> CollectionUtils.isEmpty(propertiesFilter.includes())
                || p.getProperties().stream().map(prop -> prop.propertyName()).toList()
                .containsAll(propertiesFilter.includes()))
            .toList();
      }
    }
    return result;
  }
}
