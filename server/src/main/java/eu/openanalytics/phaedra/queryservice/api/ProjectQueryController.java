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
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectQueryController {

  private final PlateServiceClient plateServiceClient;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public ProjectQueryController(PlateServiceClient plateServiceClient) {
    this.plateServiceClient = plateServiceClient;
  }

  @QueryMapping
  public List<ProjectDTO> projects(@Argument ProjectFilter projectFilter) {
    List<ProjectDTO> result = plateServiceClient.getProjects();
    logger.info("Initial project result.size(): " + result.size());
    if (projectFilter != null) {
      logger.info("projectFilter is not null!!");
      if (projectFilter.name() != null) {
        logger.info("projectFilter.name() is not nul!!");
        StringFilter filter = projectFilter.name();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith()) && p.getName().startsWith(filter.startsWith()))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith()) && p.getName().endsWith(filter.endsWith()))
            .filter(p -> StringUtils.isNotBlank(filter.contains()) && p.getName().contains(filter.contains()))
            .filter(p -> StringUtils.isNotBlank(filter.regex()) && p.getName().matches(filter.regex()))
            .toList();
        logger.info("After applying projectFilter.name() filter result.size(): " + result.size());
      }

      if (projectFilter.createdBy() != null) {
        logger.info("projectFilter.createdBy() is not nul!!");
        StringFilter filter = projectFilter.createdBy();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith()) && p.getCreatedBy().startsWith(filter.startsWith()))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith()) && p.getCreatedBy().endsWith(filter.endsWith()))
            .filter(p -> StringUtils.isNotBlank(filter.contains()) && p.getCreatedBy().contains(filter.contains()))
            .filter(p -> StringUtils.isNotBlank(filter.regex()) && p.getCreatedBy().matches(filter.regex()))
            .toList();
        logger.info("After applying projectFilter.createdBy() filter result.size(): " + result.size());
      }

      if (projectFilter.updatedBy() != null) {
        logger.info("projectFilter.updatedBy() is not nul!!");
        StringFilter filter = projectFilter.updatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith()) && p.getUpdatedBy().startsWith(filter.startsWith()))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith()) && p.getUpdatedBy().endsWith(filter.endsWith()))
            .filter(p -> StringUtils.isNotBlank(filter.contains()) && p.getUpdatedBy().contains(filter.contains()))
            .filter(p -> StringUtils.isNotBlank(filter.regex()) && p.getUpdatedBy().matches(filter.regex()))
            .toList();
        logger.info("After applying projectFilter.updatedBy() filter result.size(): " + result.size());
      }

      if (projectFilter.createdOn() != null) {
        logger.info("projectFilter.createdOn() is not nul!!");
        DateFilter filter = projectFilter.createdOn();
        result = result.stream()
            .filter(p -> filter.before() != null && p.getCreatedOn().before(filter.before()))
            .filter(p -> filter.after() != null && p.getCreatedOn().after(filter.after()))
            .filter(p -> filter.on() != null && p.getCreatedOn().equals(filter.on()))
            .toList();
        logger.info("After applying projectFilter.createdOn() filter result.size(): " + result.size());
      }

      if (projectFilter.updatedOn() != null) {
        logger.info("projectFilter.updatedOn() is not nul!!");
        DateFilter filter = projectFilter.updatedOn();
        result = result.stream()
            .filter(p -> filter.before() != null && p.getUpdatedOn().before(filter.before()))
            .filter(p -> filter.after() != null && p.getUpdatedOn().after(filter.after()))
            .filter(p -> filter.on() != null && p.getUpdatedOn().equals(filter.on()))
            .toList();
        logger.info("After applying projectFilter.updatedOn() filter result.size(): " + result.size());
      }

      if (projectFilter.tags() != null) {
        logger.info("projectFilter.tags() is not nul!!");
        MetaDataFilter filter = projectFilter.tags();
        result = result.stream()
            .filter(p -> CollectionUtils.isNotEmpty(filter.containsTags())
                && p.getTags().containsAll(filter.containsTags()))
            .toList();
        logger.info("After applying projectFilter.tags() filter result.size(): " + result.size());
      }

      if (projectFilter.properties() != null) {
        logger.info("projectFilter.properties() is not nul!!");
        MetaDataFilter filter = projectFilter.properties();
        result = result.stream()
            .filter(p -> CollectionUtils.isNotEmpty(filter.containsProperties())
                && p.getProperties().stream().map(prop -> prop.propertyName()).toList()
                .containsAll(filter.containsProperties()))
            .toList();
        logger.info("After applying projectFilter.properties() filter result.size(): " + result.size());
      }
    }
    logger.info("After filtering result.size(): " + result.size());
    return result;
  }

//  @SchemaMapping(typeName = "ProjectDTO", field = "name")
//  public List<ProjectDTO> projectName(@Argument StringFilter filter) {
//    List<ProjectDTO> result = plateServiceClient.getProjects();
//      if (filter != null) {
//        return result.stream()
//            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getName().startsWith(filter.startsWith))
//            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getName().endsWith(filter.endsWith))
//            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getName().contains(filter.contains))
//            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getName().matches(filter.regex))
//            .toList();
//      }
//      return result;
//  }
}
