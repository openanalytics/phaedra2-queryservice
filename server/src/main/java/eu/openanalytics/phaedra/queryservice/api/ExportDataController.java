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
package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.client.exception.UnresolvableObjectException;
import eu.openanalytics.phaedra.plateservice.dto.ExperimentDTO;
import eu.openanalytics.phaedra.plateservice.dto.PlateDTO;
import eu.openanalytics.phaedra.plateservice.dto.WellDTO;
import eu.openanalytics.phaedra.plateservice.dto.WellSubstanceDTO;
import eu.openanalytics.phaedra.queryservice.record.ExportPlateDataOptions;
import eu.openanalytics.phaedra.queryservice.record.ExportWellDataOptions;
import eu.openanalytics.phaedra.queryservice.record.FeatureInput;
import eu.openanalytics.phaedra.queryservice.record.FeatureStatsRecord;
import eu.openanalytics.phaedra.queryservice.record.PlateDataRecord;
import eu.openanalytics.phaedra.queryservice.record.PlateFilterOptions;
import eu.openanalytics.phaedra.queryservice.record.StatValueRecord;
import eu.openanalytics.phaedra.queryservice.record.WellDataRecord;
import eu.openanalytics.phaedra.queryservice.record.FeatureValueRecord;
import eu.openanalytics.phaedra.resultdataservice.client.ResultDataServiceClient;
import eu.openanalytics.phaedra.resultdataservice.client.exception.ResultDataUnresolvableException;
import eu.openanalytics.phaedra.resultdataservice.client.exception.ResultFeatureStatUnresolvableException;
import eu.openanalytics.phaedra.resultdataservice.client.exception.ResultSetUnresolvableException;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultDataDTO;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultFeatureStatDTO;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultSetDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ExportDataController {
  private static final int VALID_STATUS_CODE = 0;

  private final PlateServiceClient plateServiceClient;
  private final ResultDataServiceClient resultDataServiceClient;

  public ExportDataController(PlateServiceClient plateServiceClient,
      ResultDataServiceClient resultDataServiceClient) {
    this.plateServiceClient = plateServiceClient;
    this.resultDataServiceClient = resultDataServiceClient;
  }

  @QueryMapping
  public List<PlateDataRecord> exportPlateListData(@Argument ExportPlateDataOptions exportPlateDataOptions)
      throws UnresolvableObjectException, ResultFeatureStatUnresolvableException {
    ExperimentDTO experiment = plateServiceClient.getExperiment(exportPlateDataOptions.experimentId());
    List<PlateDTO> plates = plateServiceClient.getPlatesByExperiment(
        exportPlateDataOptions.experimentId());
    List<PlateDTO> filteredPlates = plates.stream()
        .filter(plate -> isPlateFilteredByOptions(exportPlateDataOptions.plateFilterOptions(), plate))
        .toList();
    Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats = new HashMap<>();
    Map<Long, List<ResultFeatureStatDTO>> wellTypeFeatureStats = new HashMap<>();
    for (PlateDTO plate : filteredPlates) {
      fetchPlateFeatureStats(exportPlateDataOptions, plate, plateFeatureStats, wellTypeFeatureStats);
    }
    return createPlateDataExportRecords(exportPlateDataOptions, experiment, filteredPlates,
        plateFeatureStats, wellTypeFeatureStats);
  }

  @QueryMapping
  public List<WellDataRecord> exportWellData(@Argument ExportWellDataOptions exportWellDataOptions)
      throws UnresolvableObjectException {
    ExperimentDTO experiment = plateServiceClient.getExperiment(exportWellDataOptions.experimentId());
    List<PlateDTO> plates = plateServiceClient.getPlatesByExperiment(
        exportWellDataOptions.experimentId());

    List<PlateDTO> filteredPlates = plates.stream()
        .filter(plate -> isPlateFilteredByOptions(exportWellDataOptions.plateFilterOptions(), plate))
        .toList();

    List<WellDataRecord> wellDataRecords = new ArrayList<>();
    filteredPlates.stream().forEach(plate -> wellDataRecords.addAll(
        createWellDataExportRecords(exportWellDataOptions, experiment, plate)));
    return wellDataRecords;
  }

  private boolean isPlateFilteredByOptions(PlateFilterOptions plateFilterOptions, PlateDTO plate) {
    boolean isValidatedByEqualToInput = Objects.isNull(plateFilterOptions.validatedBy()) || plate.getValidatedBy()
        .equals(plateFilterOptions.validatedBy());
    boolean isValidatedOnNotBeforeInputEnd = Objects.isNull(plateFilterOptions.validatedOnEnd()) || !plate.getValidatedOn()
        .before(plateFilterOptions.validatedOnEnd());
    boolean isValidatedOnNotAfterInputBegin = Objects.isNull(plateFilterOptions.validatedOnBegin()) || !plate.getValidatedOn()
        .after(plateFilterOptions.validatedOnBegin());
    boolean isApprovalStatusValid = plateFilterOptions.includeDisapprovedPlates() || plate.getApprovalStatus().getCode() >= VALID_STATUS_CODE;
    boolean isValidationStatusValid = plateFilterOptions.includeInvalidatedPlates() || plate.getValidationStatus().getCode() >= VALID_STATUS_CODE;

    return isValidatedByEqualToInput && isValidatedOnNotBeforeInputEnd && isValidatedOnNotAfterInputBegin && isApprovalStatusValid && isValidationStatusValid;
  }

  private List<PlateDataRecord> createPlateDataExportRecords(
      ExportPlateDataOptions exportPlateDataOptions,
      ExperimentDTO experiment,
      List<PlateDTO> plates, Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats,
      Map<Long, List<ResultFeatureStatDTO>> wellTypeFeatureStats) {
    return plates.stream()
        .map(plate -> createPlateExportRecord(exportPlateDataOptions, experiment, plate,
            plateFeatureStats.get(plate.getId()), wellTypeFeatureStats.get(plate.getId())))
        .toList();
  }

  private List<WellDataRecord> createWellDataExportRecords(ExportWellDataOptions exportWellDataOptions,
      ExperimentDTO experiment, PlateDTO plate) {
    List<WellDataRecord> result = new ArrayList<>();
    try {
      ResultSetDTO latestPlateResultSet = resultDataServiceClient.getLatestResultSetByPlateId(
          plate.getId());
      if (latestPlateResultSet != null) {
        Map<FeatureInput, ResultDataDTO> wellFeatureData = fetchWellFeatureData(exportWellDataOptions,
            latestPlateResultSet);
        List<WellDTO> plateWells = plateServiceClient.getWells(plate.getId());

        result.addAll(plateWells.stream()
                .filter(well -> isWellFilteredOut(exportWellDataOptions, well))
            .map(well -> createWellDataRecord(well, wellFeatureData, experiment, plate))
            .toList());
      }
    } catch (UnresolvableObjectException | ResultSetUnresolvableException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private boolean isWellFilteredOut(ExportWellDataOptions exportWellDataOptions, WellDTO well) {
    boolean isRejectionStatusValid = exportWellDataOptions.includeRejectedWells() || well.getStatus().getCode() >= VALID_STATUS_CODE;
    boolean isWellTypeValid = exportWellDataOptions.wellTypeFilter().isEmpty() || exportWellDataOptions.wellTypeFilter().contains(well.getWellType());

    return isRejectionStatusValid && isWellTypeValid;
  }

  private void fetchPlateFeatureStats(ExportPlateDataOptions exportPlateDataOptions, PlateDTO plate,
      Map<Long, List<ResultFeatureStatDTO>> plateFeatureStats,
      Map<Long, List<ResultFeatureStatDTO>> wellTypeFeatureStats) throws ResultFeatureStatUnresolvableException {
    List<ResultFeatureStatDTO> featureStats = resultDataServiceClient.getLatestResultFeatureStatsForPlateId(
        plate.getId());
    if (CollectionUtils.isNotEmpty(featureStats)) {
      if (exportPlateDataOptions.includeFeatureStats()) {
        List<ResultFeatureStatDTO> plateFeatureStatsList = featureStats.stream()
            .filter(fStats -> Objects.isNull(fStats.getWelltype())).toList();
        plateFeatureStats.put(plate.getId(), plateFeatureStatsList);
      }
      if (exportPlateDataOptions.includeWellTypeFeatureStats()) {
        List<ResultFeatureStatDTO> wellTypeFeatureStatsList = featureStats.stream()
            .filter(fStats -> Objects.nonNull(fStats.getWelltype())).toList();
        wellTypeFeatureStats.put(plate.getId(), wellTypeFeatureStatsList);
      }
    }
  }

  private Map<FeatureInput, ResultDataDTO> fetchWellFeatureData(ExportWellDataOptions exportWellDataOptions,
      ResultSetDTO latestPlateResultSet) {
    Map<FeatureInput, ResultDataDTO> results = new HashMap<>();
    for (FeatureInput selectedFeature : exportWellDataOptions.selectedFeatures()) {
      try {
        ResultDataDTO resultData = resultDataServiceClient.getResultData(latestPlateResultSet.getId(),
            selectedFeature.featureId());
        results.put(selectedFeature, resultData);
      } catch (ResultDataUnresolvableException e) {
        //TODO: Catch error correctly
      }
    }
    return results;
  }

  private WellDataRecord createWellDataRecord(WellDTO well, Map<FeatureInput, ResultDataDTO> wellFeatureData,
      ExperimentDTO experiment, PlateDTO plate) {
    List<FeatureValueRecord> featureRecords = new ArrayList<>();
    for (FeatureInput featureInput : wellFeatureData.keySet()) {
      featureRecords.add(createWellFeatureValueRecord(featureInput, wellFeatureData.get(featureInput), well));
    }
    return WellDataRecord.builder()
        .experimentId(experiment.getId())
        .experimentName(experiment.getName())
        .plateId(plate.getId())
        .barcode(plate.getBarcode())
        .validationStatus(formatEnum(plate.getValidationStatus()))
        .validatedOn(formatDate(plate.getValidatedOn()))
        .validatedBy(plate.getValidatedBy())
        .approvalStatus(formatEnum(plate.getApprovalStatus()))
        .approvedOn(formatDate(plate.getApprovedOn()))
        .approvedBy(plate.getApprovedBy())
        .wellId(well.getId())
        .wellNr(well.getWellNr())
        .rowNr(well.getRow())
        .columnNr(well.getColumn())
        .wellType(well.getWellType())
        .substanceType(getSubstanceType(well))
        .substanceName(getSubstanceName(well))
        .concentration(getConcentration(well))
        .features(featureRecords)
        .build();
  }

  private FeatureValueRecord createWellFeatureValueRecord(FeatureInput featureInput, ResultDataDTO resultData, WellDTO well) {
    return FeatureValueRecord.builder()
        .featureId(resultData.getFeatureId())
        .featureName(featureInput.featureName())
        .protocolId(featureInput.protocolId())
        .protocolName(featureInput.protocolName())
        .resultSetId(resultData.getResultSetId())
        .value(resultData.getValues()[well.getWellNr() - 1])
        .build();
  }

  private PlateDataRecord createPlateExportRecord(ExportPlateDataOptions exportPlateDataOptions,
      ExperimentDTO experiment, PlateDTO plate, List<ResultFeatureStatDTO> plateFeatureStats,
      List<ResultFeatureStatDTO> wellTypeFeatureStats) {
    return PlateDataRecord.builder()
        .experimentId(experiment.getId())
        .experimentName(experiment.getName())
        .plateId(plate.getId())
        .barcode(plate.getBarcode())
        .plateTemplateId(plate.getLinkTemplateId())
        .plateTemplateName(plate.getLinkTemplateName())
        .validationStatus(formatEnum(plate.getValidationStatus()))
        .validatedOn(formatDate(plate.getValidatedOn()))
        .validatedBy(plate.getApprovedBy())
        .approvalStatus(formatEnum(plate.getApprovalStatus()))
        .approvedOn(formatDate(plate.getApprovedOn()))
        .approvedBy(plate.getApprovedBy())
        .comment(
            String.format("%s; %s", plate.getInvalidatedReason(), plate.getDisapprovedReason()))
        .features(createFeatures(exportPlateDataOptions, plateFeatureStats, wellTypeFeatureStats))
        .build();
  }

  private StatValueRecord createStatValueRecord(ResultFeatureStatDTO fstat) {
    return StatValueRecord.builder()
        .name(fstat.getStatisticName())
        .value(fstat.getValue())
        .build();
  }

  private Optional<FeatureStatsRecord> createFeatureStatsRecord(
      List<ResultFeatureStatDTO> featureStats, FeatureInput selectedFeature,
      Optional<String> wellType) {
    if (CollectionUtils.isNotEmpty(featureStats)) {
      return Optional.of(FeatureStatsRecord.builder()
          .featureId(selectedFeature.featureId())
          .featureName(selectedFeature.featureName())
          .protocolId(selectedFeature.protocolId())
          .protocolName(selectedFeature.protocolName())
          .resultSetId(featureStats.getFirst().getResultSetId())
          .wellType(wellType.orElse(null))
          .stats(featureStats.stream().map(this::createStatValueRecord).toList())
          .build());
    }
    return Optional.empty();
  }

  private List<FeatureStatsRecord> createFeatures(ExportPlateDataOptions exportPlateDataOptions,
      List<ResultFeatureStatDTO> plateFeatureStats,
      List<ResultFeatureStatDTO> wellTypeFeatureStats) {
    List<FeatureStatsRecord> features = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(plateFeatureStats)) {
      for (FeatureInput selectedFeature : exportPlateDataOptions.selectedFeatures()) {
        List<ResultFeatureStatDTO> featureStatsFiltered = plateFeatureStats.stream()
            .filter(fStat -> fStat.getFeatureId().equals(selectedFeature.featureId()))
            .toList();

        Optional<FeatureStatsRecord> featureStatsRecord = createFeatureStatsRecord(
            featureStatsFiltered, selectedFeature, Optional.empty());
        featureStatsRecord.ifPresent(features::add);
      }
    }

    if (CollectionUtils.isNotEmpty(wellTypeFeatureStats)) {
      for (FeatureInput selectedFeature : exportPlateDataOptions.selectedFeatures()) {
        Map<String, List<ResultFeatureStatDTO>> featureStatsFiltered = wellTypeFeatureStats.stream()
            .filter(fStat -> fStat.getFeatureId().equals(selectedFeature.featureId()))
            .collect(Collectors.groupingBy(ResultFeatureStatDTO::getWelltype));

        if (MapUtils.isNotEmpty(featureStatsFiltered)) {
          for (String wellType : featureStatsFiltered.keySet()) {
            Optional<FeatureStatsRecord> featureStatsRecord = createFeatureStatsRecord(
                featureStatsFiltered.get(wellType), selectedFeature, Optional.of(wellType));
            featureStatsRecord.ifPresent(features::add);
          }
        }
      }
    }

    return features;
  }

  private String getSubstanceType(WellDTO well) {
    return Optional.ofNullable(well.getWellSubstance()).map(WellSubstanceDTO::getType).orElse(null);
  }

  private String getSubstanceName(WellDTO well) {
    return Optional.ofNullable(well.getWellSubstance()).map(WellSubstanceDTO::getName).orElse(null);
  }

  private Double getConcentration(WellDTO well) {
    return Optional.ofNullable(well.getWellSubstance()).map(WellSubstanceDTO::getConcentration)
        .orElse(null);
  }

  private String formatDate(Date date) {
    return Optional.ofNullable(date)
        .map(d -> new SimpleDateFormat("dd-MM-yyyy").format(d)).orElse(null);
  }

  private String formatEnum(Enum<?> enumValue) {
    return Optional.ofNullable(enumValue)
        .map(Enum::name).orElse(null);
  }
}

