package com.oracle.opower.heatmap.dao;

import com.oracle.opower.heatmap.config.HeatmapApplication;
import com.oracle.opower.heatmap.model.Country;
import com.oracle.opower.heatmap.model.HeatmapData;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HeatmapApplication.class})
public class HeatmapDAOTest {

    @Autowired
    private HeatmapDAO heatmapDAO;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldGetCountryList() {
        Flux<Country> countryList = this.heatmapDAO.getCountryList();
        StepVerifier.create(countryList)
                .consumeNextWith(country -> {
                    assertNotNull(country);
                });
    }

    @Test
    void shouldGetMeasurements() {
        HeatmapData measurement = this.heatmapDAO.getMeasurement(null, null, Collections.singletonList("pm25"), Collections.singletonList("US"));

        assertNotNull(measurement);
    }
}
