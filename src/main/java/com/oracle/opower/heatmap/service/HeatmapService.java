package com.oracle.opower.heatmap.service;

import com.oracle.opower.heatmap.dao.HeatmapDAO;
import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

/**
 * HeatmapService<br>
 * v0 Implementation of IHeatmapService
 */
@Service
public class HeatmapService implements IHeatmapService {

    private final NumberFormat formatter = new DecimalFormat("00.00000000");

    @Autowired
    private HeatmapDAO heatmapDAO;

    @Override
    public Flux<Country> getCountryList() {
        return heatmapDAO.getCountryList();
    }

    @Override
    public HeatmapData getMeasurementByCountry(String countryCode, String parameter) {
        return getMeasurement(null, null, null, parameter, countryCode);
    }

    @Override
    public HeatmapData getMeasurementByLocation(Float latitude, Float longitude, Integer radius, String parameter) {
        return getMeasurement(latitude, longitude, radius, parameter, null);
    }

    private HeatmapData getMeasurement(Float latitude, Float longitude, Integer radius, String parameter, String countryCode) {

        String location = null;
        if (latitude != null & longitude != null) {
            location = formatter.format(latitude) + "," + formatter.format(longitude);
        }

        List<String> countries = countryCode != null ? Collections.singletonList(countryCode) : null;
        List<Object> parameters = parameter != null ? Collections.singletonList(parameter) : null;

        return heatmapDAO.getMeasurement(location, radius, parameters, countries);
    }
}
