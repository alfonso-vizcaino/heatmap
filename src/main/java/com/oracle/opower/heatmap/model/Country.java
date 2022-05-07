package com.oracle.opower.heatmap.model;

import lombok.Data;

import java.util.*;

@Data
public class Country {
    private String countryName;
    private String countryCode;
    private List<MeasurementParam> parameters = new ArrayList<>();
    private List<Coordinate> coordinates = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country)) return false;
        Country country = (Country) o;
        return Objects.equals(countryCode, country.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode);
    }
}
