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
package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.client.exception.UnresolvableObjectException;
import eu.openanalytics.phaedra.plateservice.dto.ExperimentDTO;
import eu.openanalytics.phaedra.plateservice.dto.PlateDTO;
import eu.openanalytics.phaedra.queryservice.record.ExportDataOptions;
import eu.openanalytics.phaedra.queryservice.record.PlateExportRecord;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceClient;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ExportDataController {

  private final PlateServiceClient plateServiceClient;
  private final ResultDataServiceClient resultDataServiceClient;

  public ExportDataController(PlateServiceClient plateServiceClient,
      ResultDataServiceClient resultDataServiceClient) {
    this.plateServiceClient = plateServiceClient;
    this.resultDataServiceClient = resultDataServiceClient;
  }

  @QueryMapping
  public List<PlateExportRecord> exportPlateListData(@Argument ExportDataOptions exportDataOptions)
      throws UnresolvableObjectException {
    ExperimentDTO experiment = plateServiceClient.getExperiment(exportDataOptions.experimentId());
    List<PlateDTO> plates = plateServiceClient.getPlatesByExperiment(exportDataOptions.experimentId());

    List<PlateDTO> filteredPlates = plates.stream()
        .filter(plate -> isPlateFilteredByOptions(exportDataOptions, plate))
        .toList();

    return createPlateExportRecords(experiment, filteredPlates);
  }

  /**
   * Checks if the plate should be filtered based on the given export data options.
   *
   * @param exportDataOptions The export data options.
   * @param plate             The plate to be checked.
   * @return True if the plate should be filtered, false otherwise.
   */
  private boolean isPlateFilteredByOptions(ExportDataOptions exportDataOptions, PlateDTO plate) {
    return (Objects.isNull(exportDataOptions.validatedBy()) || plate.getValidatedBy().equals(exportDataOptions.validatedBy())) &&
        (Objects.isNull(exportDataOptions.validatedOnEnd()) || !plate.getValidatedOn().before(exportDataOptions.validatedOnEnd())) &&
        (Objects.isNull(exportDataOptions.validatedOnBegin()) || !plate.getValidatedOn().after(exportDataOptions.validatedOnBegin())) &&
        (Objects.isNull(exportDataOptions.approvedBy()) || !plate.getApprovedBy().equals(exportDataOptions.approvedBy())) &&
        (Objects.isNull(exportDataOptions.approvedOnEnd()) || !plate.getApprovedOn().before(exportDataOptions.approvedOnEnd())) &&
        (Objects.isNull(exportDataOptions.validatedOnBegin()) || !plate.getApprovedOn().after(exportDataOptions.validatedOnBegin())) &&
        (exportDataOptions.includeInvalidatedPlates() || plate.getValidationStatus().getCode() >= 0) &&
        (exportDataOptions.includeDisapprovedPlates() || plate.getApprovalStatus().getCode() >= 0);
  }

  /**
   * Creates a list of PlateExportRecord objects based on the given ExperimentDTO and plates.
   *
   * @param experiment     The ExperimentDTO object representing the experiment.
   * @param plates The List of PlateDTO objects to be mapped to PlateExportRecord objects.
   * @return A List of PlateExportRecord objects.
   */
  private List<PlateExportRecord> createPlateExportRecords(ExperimentDTO experiment, List<PlateDTO> plates) {
    return plates.stream()
        .map(plate -> createPlateExportRecord(experiment, plate))
        .toList();
  }

  /**
   * Creates a PlateExportRecord object based on the given ExperimentDTO and PlateDTO.
   *
   * @param experiment The ExperimentDTO object representing the experiment.
   * @param plate The PlateDTO object representing the plate.
   * @return A PlateExportRecord object.
   */
  private PlateExportRecord createPlateExportRecord(ExperimentDTO experiment, PlateDTO plate) {
    return PlateExportRecord.builder()
        .experimentId(experiment.getId())
        .experimentName(experiment.getName())
        .plateId(plate.getId())
        .barcode(plate.getBarcode())
        .plateTemplateId(plate.getLinkTemplateId())
        .plateTemplateName(plate.getLinkTemplateName())
        .validationStatus(Optional.ofNullable(plate.getValidationStatus()).map(Enum::name).orElse(null))
        .validatedOn(Optional.ofNullable(plate.getValidatedOn()).map(date -> new SimpleDateFormat("dd-MM-yyyy").format(date)).orElse(null))
        .validatedBy(plate.getApprovedBy())
        .approvalStatus(Optional.ofNullable(plate.getApprovalStatus()).map(Enum::name).orElse(null))
        .approvedOn(Optional.ofNullable(plate.getApprovedOn()).map(date -> new SimpleDateFormat("dd-MM-yyyy").format(date)).orElse(null))
        .approvedBy(plate.getApprovedBy())
        .comment(String.format("%s; %s", plate.getInvalidatedReason(), plate.getDisapprovedReason()))
        .build();
  }
}

