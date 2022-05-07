package com.oracle.opower.heatmap.service;

import com.oracle.opower.heatmap.config.HeatmapApplication;
import com.oracle.opower.heatmap.config.WebConfig;
import com.oracle.opower.heatmap.dao.HeatmapDAO;
import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import com.oracle.opower.heatmap.model.HeatmapEntry;
import com.oracle.opower.heatmap.model.HeatmapRange;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {HeatmapApplication.class, WebConfig.class})
public class HeatmapServiceTest {

    @MockBean
    private HeatmapDAO heatmapDAO;

    @Autowired
    private HeatmapService heatmapService;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldGetCountryList() {
        Country country = new Country();
        country.setCountryCode("US");
        country.setCountryName("United State");

        Flux<Country> countries = Flux.just(country);
        when(this.heatmapDAO.getCountryList()).thenReturn(countries);
        Flux<Country> countryList = heatmapService.getCountryList();
        StepVerifier.create(countryList).expectNext(country).verifyComplete();
    }

    @Test
    void shouldGetMeasurementByCountry() {
        HeatmapData heatmapData = HeatmapData
                .builder()
                .heatmapEntries(Arrays.asList(HeatmapEntry.builder().measurementValue(0.5).measurementUnit("pm").measurementParam("pm25").city("Alpharetta").country("US").build()))
                .ranges(Arrays.asList(HeatmapRange.builder().from(0.0d).to(5.0d).build()))
                .build();

        when(this.heatmapDAO.getMeasurement(any(), any(), any(), any())).thenReturn(heatmapData);
        HeatmapData measurementByCountry = heatmapService.getMeasurementByCountry("US", "pm25");

        assertNotNull(measurementByCountry);
        assertNotNull(measurementByCountry.getHeatmapEntries());
        assertNotNull(measurementByCountry.getHeatmapEntries().get(0));
        assertEquals(heatmapData.getHeatmapEntries().get(0).getMeasurementParam(), measurementByCountry.getHeatmapEntries().get(0).getMeasurementParam());
    }

    @Test
    void shouldGetMeasurementByLocation() {
        HeatmapData heatmapData = HeatmapData
                .builder()
                .heatmapEntries(Arrays.asList(HeatmapEntry.builder().measurementValue(0.5).measurementUnit("pm").measurementParam("pm25").city("Alpharetta").country("US").build()))
                .ranges(Arrays.asList(HeatmapRange.builder().from(0.0d).to(5.0d).build())).build();


        when(this.heatmapDAO.getMeasurement(anyString(), anyInt(), any(), any())).thenReturn(heatmapData);

        HeatmapData measurementByCountry = heatmapService.getMeasurementByLocation(3.2f,3.2f,10,"pm25");

        assertNotNull(measurementByCountry);
        assertNotNull(measurementByCountry.getHeatmapEntries());
        assertNotNull(measurementByCountry.getHeatmapEntries().get(0));
        assertEquals(heatmapData.getHeatmapEntries().get(0).getMeasurementParam(), measurementByCountry.getHeatmapEntries().get(0).getMeasurementParam());
    }
}
