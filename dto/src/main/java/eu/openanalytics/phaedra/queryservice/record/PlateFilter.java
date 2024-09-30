package eu.openanalytics.phaedra.queryservice.record;

import eu.openanalytics.phaedra.plateservice.enumeration.ApprovalStatus;
import eu.openanalytics.phaedra.plateservice.enumeration.CalculationStatus;
import eu.openanalytics.phaedra.plateservice.enumeration.UploadStatus;
import eu.openanalytics.phaedra.plateservice.enumeration.ValidationStatus;

public record PlateFilter(
    IdFilter id,
    StringFilter barcode,
    IdFilter experimentId,

    CalculationStatus calculationStatus,
    StringFilter calculatedBy,
    DateFilter calculatedOn,

    ValidationStatus validationStatus,
    StringFilter validatedBy,
    DateFilter validatedOn,

    ApprovalStatus approvalStatus,
    StringFilter approvedBy,
    DateFilter approvedOn,

    UploadStatus uploadStatus,
    StringFilter uploadedBy,
    DateFilter uploadedOn,

    DateFilter createdOn,
    StringFilter createdBy,
    DateFilter updatedOn,
    StringFilter updatedBy,

    MetaDataFilter tags,
    MetaDataFilter properties
) {

}
