package com.oracle.opower.heatmap.controller;

import com.oracle.opower.heatmap.config.HeatmapApplication;
import com.oracle.opower.heatmap.config.WebConfig;
import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import com.oracle.opower.heatmap.model.HeatmapEntry;
import com.oracle.opower.heatmap.model.HeatmapRange;
import com.oracle.opower.heatmap.service.HeatmapService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {HeatmapApplication.class, WebConfig.class})
@AutoConfigureMockMvc
public class HeatmapControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private HeatmapService heatmapService;

    @Test
    void contextLoads() {
    }

    @Test
    public void shouldGetCountryList() throws Exception {
        Country country = new Country();
        country.setCountryCode("US");
        country.setCountryName("United State");

        Flux<Country> countries = Flux.just(country);
        when(heatmapService.getCountryList()).thenReturn(countries);
        this.webTestClient.get().uri("/v1/heatmap/countries")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Country.class)
                .value(c -> c.get(0).getCountryCode(), equalTo("US"));
    }

    @Test
    public void shouldGetMeasurementsByCountry() throws Exception {
        HeatmapData heatmapData = HeatmapData
                .builder()
                .heatmapEntries(Arrays.asList(HeatmapEntry.builder().measurementValue(0.5).measurementUnit("pm").measurementParam("pm25").city("Alpharetta").country("US").build()))
                .ranges(Arrays.asList(HeatmapRange.builder().from(0.0d).to(5.0d).build())).build();

        when(heatmapService.getMeasurementByCountry(anyString(), anyString())).thenReturn(heatmapData);
        this.webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v1/heatmap/countries/{id}/{param}").build("US", "pm25"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(HeatmapData.class)
                .value(data -> data.getHeatmapEntries().get(0).getMeasurementParam(), equalTo("pm25"));
    }

    @Test
    public void shouldGetMeasurementsByLocation() throws Exception {
        HeatmapData heatmapData = HeatmapData
                .builder()
                .heatmapEntries(Arrays.asList(HeatmapEntry.builder().measurementValue(0.5).measurementUnit("pm").measurementParam("pm25").city("Alpharetta").country("US").build()))
                .ranges(Arrays.asList(HeatmapRange.builder().from(0.0d).to(5.0d).build())).build();

        when(heatmapService.getMeasurementByLocation(anyFloat(), anyFloat(), anyInt(), anyString())).thenReturn(heatmapData);
        this.webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v1/heatmap/location")
                        .queryParam("latitude", 33.3)
                        .queryParam("longitude", 33.3)
                        .queryParam("radius", 100)
                        .queryParam("param", "pm25").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(HeatmapData.class)
                .value(data -> data.getHeatmapEntries().get(0).getMeasurementParam(), equalTo("pm25"));
    }
}
