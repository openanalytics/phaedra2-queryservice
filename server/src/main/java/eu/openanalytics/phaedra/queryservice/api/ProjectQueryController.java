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
    logger.info("Initial project result.size(): " + result.size());
    if (projectFilter != null) {
      logger.info("projectFilter is not null!!");
      if (projectFilter.name() != null) {
        logger.info("projectFilter.name() is not nul!!");
        StringFilter nameFilter = projectFilter.name();
        result = result.stream()
            .filter(p -> StringUtils.isBlank(nameFilter.startsWith()) || p.getName().startsWith(nameFilter.startsWith()))
            .filter(p -> StringUtils.isBlank(nameFilter.endsWith()) || p.getName().endsWith(nameFilter.endsWith()))
            .filter(p -> StringUtils.isBlank(nameFilter.contains()) || p.getName().contains(nameFilter.contains()))
            .filter(p -> StringUtils.isBlank(nameFilter.regex()) || p.getName().matches(nameFilter.regex()))
            .toList();
        logger.info("After applying projectFilter.name() filter result.size(): " + result.size());
      }

      if (projectFilter.createdBy() != null) {
        logger.info("projectFilter.createdBy() is not nul!!");
        StringFilter createdByFilter = projectFilter.createdBy();
        result = result.stream()
            .filter(p -> StringUtils.isBlank(createdByFilter.startsWith()) || p.getCreatedBy().startsWith(createdByFilter.startsWith()))
            .filter(p -> StringUtils.isBlank(createdByFilter.endsWith()) || p.getCreatedBy().endsWith(createdByFilter.endsWith()))
            .filter(p -> StringUtils.isBlank(createdByFilter.contains()) || p.getCreatedBy().contains(createdByFilter.contains()))
            .filter(p -> StringUtils.isBlank(createdByFilter.regex()) || p.getCreatedBy().matches(createdByFilter.regex()))
            .toList();
        logger.info("After applying projectFilter.createdBy() filter result.size(): " + result.size());
      }

      if (projectFilter.updatedBy() != null) {
        logger.info("projectFilter.updatedBy() is not nul!!");
        StringFilter updatedByFilter = projectFilter.updatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isBlank(updatedByFilter.startsWith()) || p.getUpdatedBy().startsWith(updatedByFilter.startsWith()))
            .filter(p -> StringUtils.isBlank(updatedByFilter.endsWith()) || p.getUpdatedBy().endsWith(updatedByFilter.endsWith()))
            .filter(p -> StringUtils.isBlank(updatedByFilter.contains()) | p.getUpdatedBy().contains(updatedByFilter.contains()))
            .filter(p -> StringUtils.isBlank(updatedByFilter.regex()) || p.getUpdatedBy().matches(updatedByFilter.regex()))
            .toList();
        logger.info("After applying projectFilter.updatedBy() filter result.size(): " + result.size());
      }

      if (projectFilter.createdOn() != null) {
        logger.info("projectFilter.createdOn() is not nul!!");
        DateFilter createdOnFilter = projectFilter.createdOn();
        result = result.stream()
            .filter(p -> createdOnFilter.before() == null || p.getCreatedOn().before(createdOnFilter.before()))
            .filter(p -> createdOnFilter.after() == null || p.getCreatedOn().after(createdOnFilter.after()))
            .filter(p -> createdOnFilter.on() == null | p.getCreatedOn().equals(createdOnFilter.on()))
            .toList();
        logger.info("After applying projectFilter.createdOn() filter result.size(): " + result.size());
      }

      if (projectFilter.updatedOn() != null) {
        logger.info("projectFilter.updatedOn() is not nul!!");
        DateFilter updatedOnFilter = projectFilter.updatedOn();
        result = result.stream()
            .filter(p -> updatedOnFilter.before() == null || p.getUpdatedOn().before(updatedOnFilter.before()))
            .filter(p -> updatedOnFilter.after() == null || p.getUpdatedOn().after(updatedOnFilter.after()))
            .filter(p -> updatedOnFilter.on() == null || p.getUpdatedOn().equals(updatedOnFilter.on()))
            .toList();
        logger.info("After applying projectFilter.updatedOn() filter result.size(): " + result.size());
      }

      if (projectFilter.tags() != null) {
        logger.info("projectFilter.tags() is not nul!!");
        MetaDataFilter tagFilter = projectFilter.tags();
        result = result.stream()
            .filter(p -> CollectionUtils.isEmpty(tagFilter.containsTags())
                || p.getTags().containsAll(tagFilter.containsTags()))
            .toList();
        logger.info("After applying projectFilter.tags() filter result.size(): " + result.size());
      }

      if (projectFilter.properties() != null) {
        logger.info("projectFilter.properties() is not nul!!");
        MetaDataFilter propertiesFilter = projectFilter.properties();
        result = result.stream()
            .filter(p -> CollectionUtils.isEmpty(propertiesFilter.containsProperties())
                || p.getProperties().stream().map(prop -> prop.propertyName()).toList()
                .containsAll(propertiesFilter.containsProperties()))
            .toList();
        logger.info("After applying projectFilter.properties() filter result.size(): " + result.size());
      }
    }
    logger.info("After filtering result.size(): " + result.size());
    return result;
  }
}
