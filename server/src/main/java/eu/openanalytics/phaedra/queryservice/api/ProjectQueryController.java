package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.ProjectDTO;
import eu.openanalytics.phaedra.queryservice.record.DateFilter;
import eu.openanalytics.phaedra.queryservice.record.MetaDataFilter;
import eu.openanalytics.phaedra.queryservice.record.ProjectFilter;
import eu.openanalytics.phaedra.queryservice.record.StringFilter;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.collections4.CollectionUtils;
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
  public List<ProjectDTO> projects(@Argument(name = "filter") ProjectFilter projectFilter) {
    List<ProjectDTO> result = plateServiceClient.getProjects();
    if (projectFilter != null) {
      if (projectFilter.name() != null) {
        result = filterByString(result, projectFilter.name(), ProjectDTO::getName);
      }
      if (projectFilter.createdBy() != null) {
        result = filterByString(result, projectFilter.createdBy(), ProjectDTO::getCreatedBy);
      }
      if (projectFilter.updatedBy() != null) {
        result = filterByString(result, projectFilter.updatedBy(), ProjectDTO::getUpdatedBy);
      }
      if (projectFilter.createdOn() != null) {
        result = filterByDate(result, projectFilter.createdOn(), ProjectDTO::getCreatedOn);
      }
      if (projectFilter.updatedOn() != null) {
        result = filterByDate(result, projectFilter.updatedOn(), ProjectDTO::getUpdatedOn);
      }
      if (projectFilter.tags() != null) {
        result = filterByMetaData(result, projectFilter.tags(), ProjectDTO::getTags);
      }
      if (projectFilter.properties() != null) {
        result = filterByMetaData(result, projectFilter.properties(),
            projectDto -> projectDto.getProperties().stream().map(propertyDTO -> propertyDTO.propertyName())
                .toList());
      }
    }
    return result;
  }

  private List<ProjectDTO> filterByString(List<ProjectDTO> result, StringFilter stringFilter,
      Function<ProjectDTO, String> getStringFunction) {
    return result.stream()
        .filter(p -> StringUtils.isBlank(stringFilter.startsWith())
            || getStringFunction.apply(p).startsWith(stringFilter.startsWith()))
        .filter(p -> StringUtils.isBlank(stringFilter.endsWith())
            || getStringFunction.apply(p).endsWith(stringFilter.endsWith()))
        .filter(p -> StringUtils.isBlank(stringFilter.contains())
            || getStringFunction.apply(p).contains(stringFilter.contains()))
        .filter(p -> StringUtils.isBlank(stringFilter.regex())
            || getStringFunction.apply(p).matches(stringFilter.regex()))
        .toList();
  }

  private List<ProjectDTO> filterByDate(List<ProjectDTO> result, DateFilter dateFilter,
      Function<ProjectDTO, Date> getDateFunction) {
    return result.stream()
        .filter(p -> dateFilter.before() == null
            || getDateFunction.apply(p).before(dateFilter.before()))
        .filter(p -> dateFilter.after() == null
            || getDateFunction.apply(p).after(dateFilter.after()))
        .filter(p -> dateFilter.on() == null
            || getDateFunction.apply(p).equals(dateFilter.on()))
        .toList();
  }

  private List<ProjectDTO> filterByMetaData(List<ProjectDTO> result, MetaDataFilter metaDataFilter,
      Function<ProjectDTO, List<String>> getListStringFunction) {
    return result.stream()
        .filter(p -> CollectionUtils.isEmpty(metaDataFilter.includes())
            || getListStringFunction.apply(p).containsAll(metaDataFilter.includes()))
        .toList();
  }
}
