package com.oracle.opower.heatmap.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@Data
public class HeatmapData {
    private List<HeatmapEntry> heatmapEntries = new ArrayList<>();
    private List<HeatmapRange> ranges = new ArrayList<>();
}
