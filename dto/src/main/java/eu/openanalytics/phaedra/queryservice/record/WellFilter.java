package eu.openanalytics.phaedra.queryservice.record;

import eu.openanalytics.phaedra.plateservice.enumeration.WellStatus;

public record WellFilter(
    IdFilter id,
    IdFilter plateId,
    IdFilter experimentId,
    IdFilter projectId,
    StringFilter wellType,
    WellStatus status,
    StringFilter wellSubstance,
    MetaDataFilter tags,
    MetaDataFilter properties
) {

}
