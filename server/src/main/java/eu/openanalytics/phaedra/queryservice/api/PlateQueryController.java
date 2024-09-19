package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.dto.PlateDTO;
import eu.openanalytics.phaedra.queryservice.record.IdFilter;
import eu.openanalytics.phaedra.queryservice.record.PlateFilter;
import eu.openanalytics.phaedra.queryservice.utils.FilterUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PlateQueryController {

  private final PlateServiceClient plateServiceClient;

  public PlateQueryController(PlateServiceClient plateServiceClient) {
    this.plateServiceClient = plateServiceClient;
  }

  @QueryMapping
  public List<PlateDTO> plates(@Argument(name = "filter") PlateFilter plateFilter) {
    List<PlateDTO> results = new ArrayList<>();
    if (plateFilter != null) {
      if (plateFilter.experimentId() != null) {
        IdFilter filter = plateFilter.experimentId();
        if (filter.equals() != null)
          results.addAll(plateServiceClient.getPlatesByExperiment(filter.equals()));
        else if (CollectionUtils.isNotEmpty(filter.in())) {
          for (Long experimentId: filter.in())
            results.addAll(plateServiceClient.getPlatesByExperiment(experimentId));
        }
      } else {
        results.addAll(plateServiceClient.getPlates());
      }

      if (plateFilter.barcode() != null) {
        results = FilterUtils.filterByString(results, plateFilter.barcode(), PlateDTO::getBarcode);
      }
      if (plateFilter.calculatedBy() != null) {
        results = FilterUtils.filterByString(results, plateFilter.calculatedBy(), PlateDTO::getCalculatedBy);
      }
      if (plateFilter.validatedBy() != null) {
        results = FilterUtils.filterByString(results, plateFilter.validatedBy(), PlateDTO::getValidatedBy);
      }
      if (plateFilter.approvedBy() != null) {
        results = FilterUtils.filterByString(results, plateFilter.approvedBy(), PlateDTO::getApprovedBy);
      }
      if (plateFilter.uploadedBy() != null) {
        results = FilterUtils.filterByString(results, plateFilter.uploadedBy(), PlateDTO::getUploadedBy);
      }
      if (plateFilter.createdBy() != null) {
        results = FilterUtils.filterByString(results, plateFilter.createdBy(), PlateDTO::getCreatedBy);
      }
      if (plateFilter.updatedBy() != null) {
        results = FilterUtils.filterByString(results, plateFilter.updatedBy(), PlateDTO::getUpdatedBy);
      }
      if (plateFilter.calculatedOn() != null) {
        results = FilterUtils.filterByDate(results, plateFilter.calculatedOn(), PlateDTO::getCalculatedOn);
      }
      if (plateFilter.validatedOn() != null) {
        results = FilterUtils.filterByDate(results, plateFilter.validatedOn(), PlateDTO::getValidatedOn);
      }
      if (plateFilter.approvedOn() != null) {
        results = FilterUtils.filterByDate(results, plateFilter.approvedOn(), PlateDTO::getApprovedOn);
      }
      if (plateFilter.uploadedOn() != null) {
        results = FilterUtils.filterByDate(results, plateFilter.uploadedOn(), PlateDTO::getUploadedOn);
      }
      if (plateFilter.createdOn() != null) {
        results = FilterUtils.filterByDate(results, plateFilter.createdOn(), PlateDTO::getCreatedOn);
      }
      if (plateFilter.updatedOn() != null) {
        results = FilterUtils.filterByDate(results, plateFilter.updatedOn(), PlateDTO::getUpdatedOn);
      }
      if (plateFilter.calculationStatus() != null) {
        results = results.stream()
            .filter(plateDTO -> plateDTO.getCalculationStatus()
                .equals(plateFilter.calculationStatus()))
            .toList();
      }
      if (plateFilter.validationStatus() != null) {
        results = results.stream()
            .filter(plateDTO -> plateDTO.getValidationStatus()
                .equals(plateFilter.validationStatus()))
            .toList();
      }
      if (plateFilter.approvalStatus() != null) {
        results = results.stream()
            .filter(plateDTO -> plateDTO.getApprovalStatus()
                .equals(plateFilter.approvalStatus()))
            .toList();
      }
      if (plateFilter.uploadStatus() != null) {
        results = results.stream()
            .filter(plateDTO -> plateDTO.getUploadStatus()
                .equals(plateFilter.uploadStatus()))
            .toList();
      }
      if (plateFilter.tags() != null) {
        results = FilterUtils.filterByMetaData(results, plateFilter.tags(), PlateDTO::getTags);
      }
      if (plateFilter.properties() != null) {
        results = FilterUtils.filterByMetaData(results, plateFilter.properties(),
            e -> e.getProperties().stream().map(prop -> prop.propertyName()).toList());
      }

    }

    return results;
  }

}
