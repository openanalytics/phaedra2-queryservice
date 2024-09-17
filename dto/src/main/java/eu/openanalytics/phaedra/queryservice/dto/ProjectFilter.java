package eu.openanalytics.phaedra.queryservice.dto;

import lombok.Data;

@Data
public class ProjectFilter {
  private Long id;
  private StringFilter name;
  private StringFilter description;
  private DateFilter createdOn;
  private StringFilter createdBy;
  private DateFilter updatedOn;
  private StringFilter updatedBy;
  private MetaDataFilter tags;
  private MetaDataFilter properties;
}
