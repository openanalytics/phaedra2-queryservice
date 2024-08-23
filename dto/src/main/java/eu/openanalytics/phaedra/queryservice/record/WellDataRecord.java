/**
 * Phaedra II
 *
 * Copyright (C) 2016-2024 Open Analytics
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

import java.util.List;
import lombok.Builder;

@Builder
public record WellDataRecord(long experimentId,
                             String experimentName,
                             long plateId,
                             String barcode,
                             String validationStatus,
                             String validatedBy,
                             String validatedOn,
                             String approvalStatus,
                             String approvedBy,
                             String approvedOn,
                             long wellId,
                             long wellNr,
                             long rowNr,
                             long columnNr,
                             String wellType,
                             String substanceName,
                             String substanceType,
                             Double concentration,
                             Boolean isValid,
                             List<FeatureValueRecord> features) {

}
