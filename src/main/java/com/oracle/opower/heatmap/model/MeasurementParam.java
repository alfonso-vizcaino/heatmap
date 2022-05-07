package com.oracle.opower.heatmap.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

@Builder(toBuilder = true)
@ToString
@Jacksonized
@Data
public class MeasurementParam {
    private String unit;
    private String parameterName;
    private String displayName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementParam)) return false;
        MeasurementParam that = (MeasurementParam) o;
        return Objects.equals(parameterName, that.parameterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterName);
    }

    @Override
    public String toString() {
        return "MeasurementParam{" +
                "unit='" + unit + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
