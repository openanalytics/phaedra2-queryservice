package eu.openanalytics.phaedra.queryservice.record;

public record ResultDataQuery(
    IdFilter id,
    IdFilter protocolId,
    IdFilter plateId,
    IdFilter measId,
    IdFilter resultSetId,
    IdFilter featureId,
    Boolean activeMeasOnly,
    Boolean latestResultSetOnly
) {

}
