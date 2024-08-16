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
import eu.openanalytics.phaedra.queryservice.record.FeatureStatsRecord;
import eu.openanalytics.phaedra.queryservice.record.PlateDataRecord;
import eu.openanalytics.phaedra.queryservice.record.StatValueRecord;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceClient;
import eu.openanalytics.phaedra.resultdataservice.client.exception.ResultFeatureStatUnresolvableException;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultFeatureStatDTO;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
  public List<PlateDataRecord> exportPlateListData(@Argument ExportDataOptions exportDataOptions)
      throws UnresolvableObjectException, ResultFeatureStatUnresolvableException {
    ExperimentDTO experiment = plateServiceClient.getExperiment(exportDataOptions.experimentId());
    List<PlateDTO> plates = plateServiceClient.getPlatesByExperiment(exportDataOptions.experimentId());

    List<PlateDTO> filteredPlates = plates.stream()
        .filter(plate -> isPlateFilteredByOptions(exportDataOptions, plate))
        .toList();

    Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats = new HashMap<>();
    for (PlateDTO plate : filteredPlates) {
      List<ResultFeatureStatDTO> featureStats = resultDataServiceClient.getLatestResultFeatureStatsForPlateId(plate.getId());
      plateFeatureStats.put(plate.getId(), featureStats);
    }

    return createPlateExportRecords(exportDataOptions, experiment, filteredPlates, plateFeatureStats);
  }

//  @QueryMapping
//  public List<PlateDataRecord> exportWellData(@Argument ExportDataOptions exportDataOptions)
//      throws UnresolvableObjectException {
//    ExperimentDTO experiment = plateServiceClient.getExperiment(exportDataOptions.experimentId());
//    List<PlateDTO> plates = plateServiceClient.getPlatesByExperiment(exportDataOptions.experimentId());
//
//    List<PlateDTO> filteredPlates = plates.stream()
//        .filter(plate -> isPlateFilteredByOptions(exportDataOptions, plate))
//        .toList();
//
//    return createPlateExportRecords(experiment, filteredPlates);
//  }

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

  private List<PlateDataRecord> createPlateExportRecords(ExportDataOptions exportDataOptions, ExperimentDTO experiment,
      List<PlateDTO> plates, Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats) {
    return plates.stream()
        .map(plate -> createPlateExportRecord(exportDataOptions, experiment, plate, plateFeatureStats.get(plate.getId())))
        .toList();
  }

  private PlateDataRecord createPlateExportRecord(ExportDataOptions exportDataOptions, ExperimentDTO experiment, PlateDTO plate, List<ResultFeatureStatDTO> featureStats) {
    Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats = featureStats.stream().collect(
        Collectors.groupingBy(ResultFeatureStatDTO::getFeatureId));
    List<FeatureStatsRecord> features = plateFeatureStats.entrySet().stream()
        .map(entry -> {
          var selectedFeature = exportDataOptions.selectedFeatures().stream().filter(featureInput -> featureInput.featureId() == entry.getKey()).findFirst().get();
          return FeatureStatsRecord.builder()
              .featureId(entry.getKey())
              .featureName(selectedFeature.featureName())
              .protocolName(selectedFeature.protocolName())
              .stats(entry.getValue().stream().map(this::createStatValueRecord).toList())
              .build();
        })
        .toList();

    return PlateDataRecord.builder()
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
        .features(features)
        .build();
  }

  private StatValueRecord createStatValueRecord(ResultFeatureStatDTO fstat) {
    return StatValueRecord.builder()
        .name(fstat.getStatisticName())
        .value(fstat.getValue())
        .build();
  }
}

