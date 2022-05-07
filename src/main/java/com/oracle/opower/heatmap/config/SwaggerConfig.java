package com.oracle.opower.heatmap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                // .host(environment.getRequiredProperty("api.url"))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(postPaths())
                .build();
    }

    private Predicate<String> postPaths() {
        Predicate<String> or = regex("/v1/heatmap.*");
        return or;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("OpenAQ assessment using reactive programming ")
                .description("Wrapped OpenAQ API to create heatmaps")
                //.termsOfServiceUrl("http://hantsy.blogspot.com")
                .contact(new Contact("Alfonso Vizcaino", "https://github.com/alfonso-vizcaino/", "ai.alfonso.vizcaino@gmail.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("1.0")
                .build();
    }
}
