package com.oracle.opower.heatmap.dao;

import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * IHeatmapDAO<br>
 * Main contract specification to interact with OpenAQ
 */
public interface IHeatmapDAO {
    /**
     * Consults OpenAq to build a list of <code>Country</code> and provides it in a reactive manner <br>
     * The list of countries is built by accessing:<br>
     * https://docs.openaq.org/#/v2/locations_get_v2_locations_get<br>
     * https://docs.openaq.org/#/v2/countries_get_v2_countries_get<br>
     * https://docs.openaq.org/#/v2/parameters_get_v2_parameters_get<br>
     *
     * @return <code>Flux{@literal <}Country{@literal >}</code>
     */
    Flux<Country> getCountryList();

    /**
     * Consults OpenAq to build a <code>HeatmapData</code> according to the params criteria<br>
     * The heatmap is built by accessing:<br>
     * https://docs.openaq.org/#/v2/measurements_get_v2_measurements_get<br>
     *
     * @param location  {@code String} Latitude and longitude information in decimal representation. Format must comply with regex `^-?\d{1,2}\.?\d{0,8},-?1?\d{1,2}\.?\d{0,8}$`<br>
     * @param radius    {@code Integer} Radius <br>
     * @param parameter <code>List{@literal <}String{@literal >}</code> Air quality measure parameter to be used <br>
     * @param countries <code>List{@literal <}String{@literal >}</code> Two letter string list containing country codes <br>
     * @return <code>HeatmapData</code>
     */
    HeatmapData getMeasurement(String location, Integer radius, List<Object> parameter, List<String> countries);
}
