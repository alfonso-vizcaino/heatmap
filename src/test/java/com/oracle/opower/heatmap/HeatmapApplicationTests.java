package com.oracle.opower.heatmap;

import com.oracle.opower.heatmap.config.HeatmapApplication;
import com.oracle.opower.heatmap.config.SwaggerConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HeatmapApplication.class, SwaggerConfig.class})
class HeatmapApplicationTests {

    @Test
    void contextLoads() {
    }
}
