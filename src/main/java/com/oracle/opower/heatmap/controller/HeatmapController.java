package com.oracle.opower.heatmap.controller;


import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import com.oracle.opower.heatmap.service.HeatmapService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/v1/heatmap")
public class HeatmapController {

    @Autowired
    private HeatmapService heatmapService;

    @GetMapping(path = "/countries", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Country> countryWithLocationList() {
        return heatmapService.getCountryList();
    }

    @GetMapping(path = "/countries/{id}/{param}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HeatmapData measurementsByCountry(
            @ApiParam(name = "id", value="Two letter country code. regexp = ([A-Z]{2})")
            @Pattern(regexp = "([A-Z]{2})")
            @PathVariable("id") String id,
            @ApiParam(value = "Air quality measurement param")
            @PathVariable("param") String measurementParam) {
        return heatmapService.getMeasurementByCountry(id, measurementParam);
    }

    @GetMapping(path = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public HeatmapData measurementsByLocation(
            @ApiParam(name = "latitude", value = "Latitude in decimal format. Range[-199.999999, 199.9999999]", allowableValues = "range[-199.999999, 199.9999999]")
            @RequestParam("latitude") Float latitude,

            @ApiParam(name = "longitude", value = "Longitude in decimal format. Range[-199.999999, 199.9999999", allowableValues = "range[-199.999999, 199.9999999]")
            @RequestParam("longitude") Float longitude,

            @ApiParam(name = "radius", value="range[0, infinity]", allowableValues = "range[0, infinity]" )
            @RequestParam("radius") Integer radius,

            @ApiParam(value = "Air quality measurement param")
            @RequestParam("param") String measurementParam) {
        return heatmapService.getMeasurementByLocation(latitude, longitude, radius, measurementParam);
    }

}
