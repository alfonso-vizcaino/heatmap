package com.oracle.opower.heatmap.service;

import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import reactor.core.publisher.Flux;

/**
 * IHeatmapService<br>
 * Main contract specification to build heatmap information
 */
public interface IHeatmapService {

    /**
     * Provides a list of <code>Country</code> that can be consulted to fetch air quality
     *
     * @return <code>List<Country></code>
     */
    Flux<Country> getCountryList();

    /**
     * Provides air quality information in the form of a heatmap for all locations of a given <code>countryCode</code>
     * measured with <code>parameter</code>
     *
     * @param countryCode Two-letter country code {@link #getCountryList() getCountryList}
     * @param parameter   Measurement parameter for air quality {@link #getCountryList() parameterName}
     * @return <code>HeatmapData</code>
     */
    HeatmapData getMeasurementByCountry(String countryCode, String parameter);

    /**
     * Provides air quality information in the form of a heatmap for all locations within the <code>radius</code> of a
     * given coordinate measured with <code>parameter</code>
     *
     * @param latitude  latitude
     * @param longitude longitude
     * @param radius    radius
     * @param parameter Measurement parameter for air quality {@link #getCountryList() parameterName}
     * @return <code>HeatmapData</code>
     */
    HeatmapData getMeasurementByLocation(Float latitude, Float longitude, Integer radius, String parameter);
}
