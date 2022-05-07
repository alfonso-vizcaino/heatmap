package com.oracle.opower.heatmap.config;

import com.oracle.opower.openaq.api.DefaultApi;
import com.oracle.opower.openaq.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAqConfig {

    @Autowired
    RestTemplateBuilder builder;

    @Value("${openAq.baseUrl}")
    private String openAqUrl;

    @Bean
    public DefaultApi openAqApi() {
        return new DefaultApi(apiClient());
    }

    @Bean
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(openAqUrl);
        return apiClient;
    }

    @Bean
    public RestTemplate restTemplate() {
        return builder.build();
    }

}
