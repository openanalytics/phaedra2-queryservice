package eu.openanalytics.phaedra.queryservice.record;

import java.util.List;

public record IdFilter(Long equals, List<Long> in) {
}
