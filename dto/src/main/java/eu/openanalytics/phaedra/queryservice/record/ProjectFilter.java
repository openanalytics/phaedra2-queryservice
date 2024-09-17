package eu.openanalytics.phaedra.queryservice.record;

public record ProjectFilter (
  StringFilter name,
  DateFilter createdOn,
  StringFilter createdBy,
  DateFilter updatedOn,
  StringFilter updatedBy,
  MetaDataFilter tags,
  MetaDataFilter properties) {}
