package eu.openanalytics.phaedra.queryservice.record;

public record ExperimentFilter(
    StringFilter name,
    IdFilter projectId,
    StringFilter projectName,
    DateFilter createdOn,
    StringFilter createdBy,
    DateFilter updatedOn,
    StringFilter updatedBy,
    MetaDataFilter tags,
    MetaDataFilter properties
) {

}
