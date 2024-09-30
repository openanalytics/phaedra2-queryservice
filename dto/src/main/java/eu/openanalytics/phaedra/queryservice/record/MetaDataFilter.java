package eu.openanalytics.phaedra.queryservice.record;

import java.util.List;

public record MetaDataFilter (
  List<String> includes
) {}
