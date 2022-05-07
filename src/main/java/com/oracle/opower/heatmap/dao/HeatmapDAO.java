package com.oracle.opower.heatmap.dao;

import com.oracle.opower.heatmap.model.*;
import com.oracle.opower.openaq.api.V2Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class HeatmapDAO implements IHeatmapDAO {

    @Value("${opower.heatmapSlots}")
    public int total_heatmap_slots = 3;

    private static final Logger logger = Logger.getLogger(String.valueOf(HeatmapDAO.class));
    @Autowired
    private V2Api openAqApí;

    @Override
    public Flux<Country> getCountryList() {

        Mono<List<Country>> collectionMono = Mono.zip(
                Mono.just(returnLocationCountries()),
                Mono.just(returnCountries()),
                Mono.just(returnParameters())
        ).flatMap(map -> {
            Set<Country> locations = map.getT1();
            Map<String, Country> countries = map.getT2();
            Map<String, MeasurementParam> params = map.getT3();

            locations.forEach(location -> {
                Country country = countries.get(location.getCountryCode().toLowerCase());
                if (country != null) {
                    country.setParameters(location.getParameters());
                    if (country.getCoordinates().size() == 0) {
                        country.setCoordinates(location.getCoordinates());
                    }
                }
            });

            Collection<Country> allCountries = countries.values();

            //Not all countries have full param information, if so complete it
            allCountries.forEach(country -> {
                List<MeasurementParam> parameters = country.getParameters();
                if (parameters.get(0).getDisplayName() == null) {
                    parameters.forEach(param -> {
                        MeasurementParam measurementParam = params.get(param.getParameterName().toLowerCase());
                        if (measurementParam != null) {
                            param.setDisplayName(measurementParam.getDisplayName());
                            param.setUnit(measurementParam.getUnit());
                        } else {
                            //What can we do? Not consistent info. Just log it
                            logger.log(Level.WARNING, "Incomplete Param Information:\n" + param);
                        }
                    });
                }
            });

            return Mono.just(new ArrayList<>(allCountries));
        });

        return collectionMono.flatMapMany(Flux::fromIterable);
    }

    @Override
    public HeatmapData getMeasurement(String location, Integer radius, List<Object> parameters, List<String> countries) {
        Map response = (LinkedHashMap) openAqApí.measurementsGetV2MeasurementsGet(null, null, null, null, null, null, "desc", null, null, parameters, null, location, radius, null, countries, null, null, null, "datetime", null, null, null, null, null, null, null, null);
        List<LinkedHashMap<String, Object>> results = (List<LinkedHashMap<String, Object>>) response.get("results");

        List<Number> allValues = new ArrayList<>();
        Set<Integer> distinctLocations = new HashSet<>();
        List<HeatmapEntry> heatmapEntries = results.stream().map(result -> {
            if (distinctLocations.add((Integer) result.get("locationId"))) {
                allValues.add((Number) result.get("value"));
                LinkedHashMap<String, Object> coordinates = (LinkedHashMap<String, Object>) result.get("coordinates");

                return HeatmapEntry.builder().measurementUnit((String) result.get("unit")).
                        measurementValue((Number) result.get("value")).
                        measurementParam((String) result.get("parameter")).
                        location((Integer) result.get("locationId")).
                        city((String) result.get("city")).
                        locationName((String) result.get("location")).
                        country((String) result.get("country")).
                        coordinates(Coordinate.builder().latitude((Number) coordinates.get("latitude")).longitude((Number) coordinates.get("longitude")).build()).
                        build();
            } else {
                return null;
            }

        }).filter(Objects::nonNull).collect(Collectors.toList());

        double max = allValues.stream().max(Comparator.comparing(Number::doubleValue)).orElse(0).doubleValue();
        double min = allValues.stream().min(Comparator.comparing(Number::doubleValue)).orElse(0).doubleValue();

        double range = max - min;
        double slotRange = range / total_heatmap_slots;

        List<HeatmapRange> ranges = new ArrayList<>();
        for (int i = 0; i < total_heatmap_slots; i++) {
            ranges.add(HeatmapRange.builder().from((slotRange * i) + min).to((slotRange * (i + 1)) + min).build());
        }

        heatmapEntries.forEach(heatmapEntry -> {
            double normalized = heatmapEntry.getMeasurementValue().doubleValue() * range / max;
            int slot = (int) (normalized / (slotRange == 0 ? total_heatmap_slots : slotRange)); //Defense programming
            heatmapEntry.setHeatmapPos(slot == total_heatmap_slots ? total_heatmap_slots - 1 : slot);
        });

        return HeatmapData.builder().heatmapEntries(heatmapEntries).ranges(ranges).build();
    }

    private Set<Country> returnLocationCountries() {
        Map<String, Country> countries = new LinkedHashMap<>();
        openAqApí.locationsGetV2LocationsGet(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null).
                getResults().forEach(row -> {
                    LinkedHashMap<String, Object> countryMapInfo = (LinkedHashMap<String, Object>) row;
                    String countryCode = (String) countryMapInfo.get("country");
                    if (countryCode != null) {
                        Country country = countries.get(countryCode.toLowerCase());
                        if (country == null) {
                            country = new Country();
                            country.setCountryCode(countryCode);
                        }

                        List<Coordinate> coordinates = getCoordinates(countryMapInfo);
                        Set<MeasurementParam> parameters = getMeasurementParams(countryMapInfo);
                        country.getParameters().addAll(parameters);
                        country.getCoordinates().addAll(coordinates);

                        countries.put(countryCode.toLowerCase(), country);
                    }
                });

        return new HashSet<>(countries.values());
    }


    private List<Coordinate> getCoordinates(LinkedHashMap<String, Object> countryMapInfo) {
        List<Coordinate> coordinates = new ArrayList<>();
        List<Double> rawCoordinates = (ArrayList<Double>) countryMapInfo.get("bounds");
        if (rawCoordinates != null) {
            for (int i = 0; i < rawCoordinates.size(); i++) {
                Coordinate coordinate = Coordinate.builder().longitude(rawCoordinates.get(i)).latitude(rawCoordinates.get(++i)).build();
                coordinates.add(coordinate);
            }
        } else {
            LinkedHashMap<String, Object> rawCoordinateMap = (LinkedHashMap<String, Object>) countryMapInfo.get("coordinates");
            if (rawCoordinateMap != null) {
                Coordinate coordinate = Coordinate.builder().longitude((Double) rawCoordinateMap.get("longitude")).latitude((Double) rawCoordinateMap.get("latitude")).build();
                coordinates.add(coordinate);
            }
        }

        return coordinates;
    }


    private Map<String, Country> returnCountries() {
        Map<String, Country> countries = new LinkedHashMap<>();
        openAqApí.countriesGetV2CountriesGet(200, 1, 0, null, null, null, null).
                getResults().forEach(countryRow -> {

                    Country country = new Country();
                    country.setCountryCode(countryRow.getCode());
                    country.setCountryName(countryRow.getName());
                    //Remove duplicates by storing them in a Set
                    Set<MeasurementParam> parameters = new HashSet<>();
                    countryRow.getParameters().forEach(param -> parameters.add(MeasurementParam.builder().parameterName(param).build()));
                    country.setParameters(new ArrayList<>(parameters));

                    countries.put(countryRow.getCode().toLowerCase(), country);
                });

        return countries;
    }

    private Map<String, MeasurementParam> returnParameters() {
        Map<String, MeasurementParam> params = new HashMap<>();
        openAqApí.parametersGetV2ParametersGet(null, null, null, null, null, null, null, null).
                getResults().forEach(parameterRow -> {
                    MeasurementParam param = MeasurementParam.builder().
                            parameterName(parameterRow.getName()).
                            displayName(parameterRow.getDisplayName()).
                            unit(parameterRow.getPreferredUnit()).build();
                    params.put(param.getParameterName().toLowerCase(), param);
                });

        return params;
    }

    private Set<MeasurementParam> getMeasurementParams(LinkedHashMap<String, Object> countryMapInfo) {
        Set<MeasurementParam> parameters = new HashSet<>();
        List<LinkedHashMap<String, Object>> rawParams = (ArrayList<LinkedHashMap<String, Object>>) countryMapInfo.get("parameters");
        if (rawParams != null) {
            rawParams.forEach(paramMap -> parameters.add(MeasurementParam.builder().parameterName((String) paramMap.get("parameter")).
                    displayName((String) paramMap.get("displayName")).
                    unit((String) paramMap.get("unit")).build()
            ));
        }

        return parameters;
    }
}
