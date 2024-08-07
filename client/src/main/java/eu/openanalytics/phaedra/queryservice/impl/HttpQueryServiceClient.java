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
package eu.openanalytics.phaedra.queryservice.impl;

import eu.openanalytics.phaedra.queryservice.QueryServiceClient;
import eu.openanalytics.phaedra.util.auth.IAuthorizationService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class HttpQueryServiceClient implements QueryServiceClient {
  private final RestTemplate restTemplate;
  private final IAuthorizationService authService;
  private final UrlFactory urlFactory;

  private static final String PROP_BASE_URL = "phaedra.query-service.base-url";
  private static final String DEFAULT_BASE_URL = "http://phaedra-query-service:8080/phaedra/query-service";

  public HttpQueryServiceClient(RestTemplate restTemplate, IAuthorizationService authService, Environment environment) {
    this.restTemplate = restTemplate;
    this.authService = authService;
    this.urlFactory = new UrlFactory(environment.getProperty(PROP_BASE_URL, DEFAULT_BASE_URL));
  }

  private HttpHeaders makeHttpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    String bearerToken = authService.getCurrentBearerToken();
    if (bearerToken != null) httpHeaders.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", bearerToken));
    return httpHeaders;
  }
}
