package eu.openanalytics.phaedra.queryservice.api;

import eu.openanalytics.phaedra.queryservice.record.ResultDataQuery;
import eu.openanalytics.phaedra.queryservice.service.QueryService;
import eu.openanalytics.phaedra.resultdataservice.dto.ResultDataDTO;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ResultDataQueryController {

  private final QueryService queryService;

  public ResultDataQueryController(QueryService queryService) {
    this.queryService = queryService;
  }

  @QueryMapping
  public List<ResultDataDTO> resultData(@Argument ResultDataQuery query) {
    return queryService.queryResultData(query);
  }
}
