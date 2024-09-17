package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.ProjectDTO;
import eu.openanalytics.phaedra.queryservice.dto.DateFilter;
import eu.openanalytics.phaedra.queryservice.dto.MetaDataFilter;
import eu.openanalytics.phaedra.queryservice.dto.ProjectFilter;
import eu.openanalytics.phaedra.queryservice.dto.StringFilter;
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
      if (projectFilter.getName() != null) {
        logger.info("projectFilter.getName() is not nul!!");
        StringFilter filter = projectFilter.getName();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getName().startsWith(filter.startsWith))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getName().endsWith(filter.endsWith))
            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getName().contains(filter.contains))
            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getName().matches(filter.regex))
            .toList();
        logger.info("After applying projectFilter.getName() filter result.size(): " + result.size());
      }

      if (projectFilter.getCreatedBy() != null) {
        logger.info("projectFilter.getCreatedBy() is not nul!!");
        StringFilter filter = projectFilter.getCreatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getCreatedBy().startsWith(filter.startsWith))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getCreatedBy().endsWith(filter.endsWith))
            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getCreatedBy().contains(filter.contains))
            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getCreatedBy().matches(filter.regex))
            .toList();
        logger.info("After applying projectFilter.getCreatedBy() filter result.size(): " + result.size());
      }

      if (projectFilter.getUpdatedBy() != null) {
        logger.info("projectFilter.getUpdatedBy() is not nul!!");
        StringFilter filter = projectFilter.getUpdatedBy();
        result = result.stream()
            .filter(p -> StringUtils.isNotBlank(filter.startsWith) && p.getUpdatedBy().startsWith(filter.startsWith))
            .filter(p -> StringUtils.isNotBlank(filter.endsWith) && p.getUpdatedBy().endsWith(filter.endsWith))
            .filter(p -> StringUtils.isNotBlank(filter.contains) && p.getUpdatedBy().contains(filter.contains))
            .filter(p -> StringUtils.isNotBlank(filter.regex) && p.getUpdatedBy().matches(filter.regex))
            .toList();
        logger.info("After applying projectFilter.getUpdatedBy() filter result.size(): " + result.size());
      }

      if (projectFilter.getCreatedOn() != null) {
        logger.info("projectFilter.getCreatedOn() is not nul!!");
        DateFilter filter = projectFilter.getCreatedOn();
        result = result.stream()
            .filter(p -> filter.before != null && p.getCreatedOn().before(filter.before))
            .filter(p -> filter.after != null && p.getCreatedOn().after(filter.after))
            .filter(p -> filter.on != null && p.getCreatedOn().equals(filter.on))
            .toList();
        logger.info("After applying projectFilter.getCreatedOn() filter result.size(): " + result.size());
      }

      if (projectFilter.getUpdatedOn() != null) {
        logger.info("projectFilter.getUpdatedOn() is not nul!!");
        DateFilter filter = projectFilter.getUpdatedOn();
        result = result.stream()
            .filter(p -> filter.before != null && p.getUpdatedOn().before(filter.before))
            .filter(p -> filter.after != null && p.getUpdatedOn().after(filter.after))
            .filter(p -> filter.on != null && p.getUpdatedOn().equals(filter.on))
            .toList();
        logger.info("After applying projectFilter.getUpdatedOn() filter result.size(): " + result.size());
      }

      if (projectFilter.getTags() != null) {
        logger.info("projectFilter.getTags() is not nul!!");
        MetaDataFilter filter = projectFilter.getTags();
        result = result.stream()
            .filter(p -> CollectionUtils.isNotEmpty(filter.containsTags)
                && p.getTags().containsAll(filter.containsTags))
            .toList();
        logger.info("After applying projectFilter.getTags() filter result.size(): " + result.size());
      }

      if (projectFilter.getProperties() != null) {
        logger.info("projectFilter.getProperties() is not nul!!");
        MetaDataFilter filter = projectFilter.getProperties();
        result = result.stream()
            .filter(p -> CollectionUtils.isNotEmpty(filter.containsProperties)
                && p.getProperties().stream().map(prop -> prop.propertyName()).toList()
                .containsAll(filter.containsProperties))
            .toList();
        logger.info("After applying projectFilter.getProperties() filter result.size(): " + result.size());
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
