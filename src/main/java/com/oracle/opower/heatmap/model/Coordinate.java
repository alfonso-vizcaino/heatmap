package com.oracle.opower.heatmap.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@Data
public class Coordinate {
    private Number latitude;
    private Number longitude;
}
