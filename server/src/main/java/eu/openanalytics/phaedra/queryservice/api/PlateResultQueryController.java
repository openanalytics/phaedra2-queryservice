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

import eu.openanalytics.phaedra.queryservice.model.PlateResultData;
import eu.openanalytics.phaedra.queryservice.service.PlateResultDataService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PlateResultQueryController {

    private final PlateResultDataService plateResultDataService;

    public PlateResultQueryController(PlateResultDataService plateResultDataService) {
        this.plateResultDataService = plateResultDataService;
    }

    @QueryMapping
    public PlateResultData plateResultData(@Argument Long plateId) {
        return plateResultDataService.getPlateResultData(plateId);
    }
}
