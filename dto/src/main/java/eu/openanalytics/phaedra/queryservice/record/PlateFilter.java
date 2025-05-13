/**
 * Phaedra II
 *
 * Copyright (C) 2016-2025 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
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
