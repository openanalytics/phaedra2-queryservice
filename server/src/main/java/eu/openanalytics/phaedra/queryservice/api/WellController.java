package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.plateservice.client.PlateServiceClient;
import eu.openanalytics.phaedra.plateservice.client.exception.UnresolvableObjectException;
import eu.openanalytics.phaedra.plateservice.dto.WellDTO;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/well")
public class WellController {

  private final PlateServiceClient plateServiceClient;

  public WellController(PlateServiceClient plateServiceClient) {
    this.plateServiceClient = plateServiceClient;
  }

  @GetMapping
  public ResponseEntity<List<WellDTO>> getWells(@RequestParam(required = false) Integer nrOfWells)
      throws UnresolvableObjectException {
    List<WellDTO> response = plateServiceClient.getNWells(nrOfWells);
    if (CollectionUtils.isNotEmpty(response)) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.ok(Collections.emptyList());
  }
}
