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
import eu.openanalytics.phaedra.queryservice.record.FeatureInput;
import eu.openanalytics.phaedra.queryservice.record.FeatureStatsRecord;
import eu.openanalytics.phaedra.queryservice.record.PlateDataRecord;
import eu.openanalytics.phaedra.queryservice.record.StatValueRecord;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceClient;
import eu.openanalytics.phaedra.resultdataservice.client.exception.ResultFeatureStatUnresolvableException;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultFeatureStatDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ExportDataController {

  private final PlateServiceClient plateServiceClient;
  private final ResultDataServiceClient resultDataServiceClient;

  private final Logger logger = LoggerFactory.getLogger(getClass());

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
    Map<Long, List<ResultFeatureStatDTO>> wellTypeFeatureStats = new HashMap<>();
    for (PlateDTO plate : filteredPlates) {
      List<ResultFeatureStatDTO> featureStats = resultDataServiceClient.getLatestResultFeatureStatsForPlateId(plate.getId());
      logger.info(String.format("Number of feature stats for plate %d: %d", plate.getId(), featureStats.size())) ;

      if (CollectionUtils.isNotEmpty(featureStats)) {
        if (exportDataOptions.includeFeatureStats()) {
          List<ResultFeatureStatDTO> result = featureStats.stream().filter(fStats -> Objects.isNull(fStats.getWelltype())).toList();
          logger.info(String.format("Number of plate feature stats for plate %d: %d", plate.getId(), result.size())) ;
          plateFeatureStats.put(plate.getId(), result);
        }

        if (exportDataOptions.includeWellTypeFeatureStats()) {
          List<ResultFeatureStatDTO> result = featureStats.stream().filter(fStats -> !Objects.isNull(fStats.getWelltype())).toList();
          logger.info(String.format("Number of well type feature stats for plate %d: %d", plate.getId(), result.size())) ;
          wellTypeFeatureStats.put(plate.getId(), result);
        }
      }
    }

    return createPlateExportRecords(exportDataOptions, experiment, filteredPlates,
        plateFeatureStats, wellTypeFeatureStats);
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
      List<PlateDTO> plates, Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats,
      Map<Long, List<ResultFeatureStatDTO>> wellTypeFeatureStats) {
    return plates.stream()
        .map(plate -> createPlateExportRecord(exportDataOptions, experiment, plate,
            plateFeatureStats.get(plate.getId()), wellTypeFeatureStats.get(plate.getId())))
        .toList();
  }

  private PlateDataRecord createPlateExportRecord(ExportDataOptions exportDataOptions,
      ExperimentDTO experiment, PlateDTO plate, List<ResultFeatureStatDTO> plateFeatureStats,
      List<ResultFeatureStatDTO> wellTypeFeatureStats) {
    return PlateDataRecord.builder()
        .experimentId(experiment.getId())
        .experimentName(experiment.getName())
        .plateId(plate.getId())
        .barcode(plate.getBarcode())
        .plateTemplateId(plate.getLinkTemplateId())
        .plateTemplateName(plate.getLinkTemplateName())
        .validationStatus(Optional.ofNullable(plate.getValidationStatus())
            .map(Enum::name).orElse(null))
        .validatedOn(Optional.ofNullable(plate.getValidatedOn())
            .map(date -> new SimpleDateFormat("dd-MM-yyyy").format(date)).orElse(null))
        .validatedBy(plate.getApprovedBy())
        .approvalStatus(Optional.ofNullable(plate.getApprovalStatus())
            .map(Enum::name).orElse(null))
        .approvedOn(Optional.ofNullable(plate.getApprovedOn())
            .map(date -> new SimpleDateFormat("dd-MM-yyyy").format(date)).orElse(null))
        .approvedBy(plate.getApprovedBy())
        .comment(String.format("%s; %s", plate.getInvalidatedReason(), plate.getDisapprovedReason()))
        .features(createFeatures(exportDataOptions, plateFeatureStats, wellTypeFeatureStats))
        .build();
  }

  private StatValueRecord createStatValueRecord(ResultFeatureStatDTO fstat) {
    return StatValueRecord.builder()
        .name(fstat.getStatisticName())
        .value(fstat.getValue())
        .build();
  }

  private Optional<FeatureStatsRecord> createFeatureStatsRecord(List<ResultFeatureStatDTO> featureStats, FeatureInput selectedFeature) {
    List<ResultFeatureStatDTO> featureStatsFiltered = featureStats.stream()
        .filter(fStat -> fStat.getFeatureId().equals(selectedFeature.featureId()))
        .toList();
    if (CollectionUtils.isNotEmpty(featureStatsFiltered)) {
      return Optional.of(FeatureStatsRecord.builder()
          .featureId(selectedFeature.featureId())
          .featureName(selectedFeature.featureName())
          .protocolName(selectedFeature.protocolName())
          .stats(featureStatsFiltered.stream().map(this::createStatValueRecord).toList())
          .build());
    }
    return Optional.empty();
  }

  private Optional<FeatureStatsRecord> createWellTypeFeatureStatsRecord(String wellType,
      List<ResultFeatureStatDTO> featureStats, FeatureInput selectedFeature) {
    if (CollectionUtils.isNotEmpty(featureStats)) {
      logger.info(
          String.format("Nr of stats for well type '%s': %d", wellType, featureStats.size()));
      return Optional.of(FeatureStatsRecord.builder()
          .featureId(selectedFeature.featureId())
          .featureName(selectedFeature.featureName())
          .protocolName(selectedFeature.protocolName())
          .resultSetId(featureStats.get(0).getResultSetId())
          .wellType(wellType)
          .stats(featureStats.stream().map(this::createStatValueRecord).toList())
          .build());
    }
    return Optional.empty();
  }

  private List<FeatureStatsRecord> createFeatures(ExportDataOptions exportDataOptions,
      List<ResultFeatureStatDTO> plateFeatureStats,
      List<ResultFeatureStatDTO> wellTypeFeatureStats) {
    List<FeatureStatsRecord> features = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(plateFeatureStats)) {
      for (FeatureInput selectedFeature : exportDataOptions.selectedFeatures()) {
        Optional<FeatureStatsRecord> featureStatsRecord = createFeatureStatsRecord(
            plateFeatureStats, selectedFeature);
        featureStatsRecord.ifPresent(features::add);
      }
    }

    if (CollectionUtils.isNotEmpty(wellTypeFeatureStats)) {
      for (FeatureInput selectedFeature : exportDataOptions.selectedFeatures()) {
        Map<String, List<ResultFeatureStatDTO>> featureStatsFiltered = wellTypeFeatureStats.stream()
            .filter(fStat -> fStat.getFeatureId().equals(selectedFeature.featureId()))
            .collect(Collectors.groupingBy(ResultFeatureStatDTO::getWelltype));

        if (MapUtils.isNotEmpty(featureStatsFiltered)) {
          for (String wellType : featureStatsFiltered.keySet()) {
            Optional<FeatureStatsRecord> featureStatsRecord = createWellTypeFeatureStatsRecord(
                wellType, featureStatsFiltered.get(wellType), selectedFeature);
            featureStatsRecord.ifPresent(features::add);
          }
        }
      }
    }

    return features;
  }
}

