package eu.openanalytics.phaedra.queryservice.record;

public record StringFilter (
  String startsWith,
  String endsWith,
  String contains,
  String regex
  ) {}
