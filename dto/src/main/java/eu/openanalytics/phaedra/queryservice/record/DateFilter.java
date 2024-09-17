package eu.openanalytics.phaedra.queryservice.record;

import java.util.Date;

public record DateFilter(
    Date before,
    Date after,
    Date on) {}
