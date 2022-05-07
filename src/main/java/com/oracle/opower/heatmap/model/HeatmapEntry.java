package com.oracle.opower.heatmap.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@Data
public class HeatmapEntry {
    private String measurementParam;
    private Number measurementValue;
    private String measurementUnit;
    private Integer location;
    private String city;
    private String locationName;
    private String country;
    private Integer heatmapPos;
    private Coordinate coordinates;
}
