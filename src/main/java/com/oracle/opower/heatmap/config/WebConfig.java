package com.oracle.opower.heatmap.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.types.ResolvedArrayType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

@Configuration
@EnableWebMvc
@ComponentScan("com.oracle.opower*")
public class WebConfig implements WebMvcConfigurer {
    //
    @Bean
    UrlBasedViewResolver resolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        WebMvcConfigurer.super.addViewControllers(registry);
    }

    @Bean
    @Primary
    //https://stackoverflow.com/questions/57736856/how-to-configure-springfox-to-unwrap-reactive-types-such-as-mono-and-flux-withou
    public HandlerMethodResolver fluxMethodResolver(TypeResolver resolver) {
        return new HandlerMethodResolver(resolver) {
            @Override
            public ResolvedType methodReturnType(HandlerMethod handlerMethod) {
                ResolvedType retType = super.methodReturnType(handlerMethod);
                // we unwrap Mono, Flux, and as a bonus - ResponseEntity
                while (
                        retType.getErasedType() == Mono.class
                                || retType.getErasedType() == Flux.class
                                || retType.getErasedType() == ResponseEntity.class
                ) {
                    ResolvedType boundType = retType.getTypeBindings().getBoundType(0);
                    if (retType.getErasedType() == Flux.class) {
                        // treat it as an array
                        retType = new ResolvedArrayType(boundType.getErasedType(), boundType.getTypeBindings(), boundType);
                    } else {
                        retType = boundType;
                    }
                }

                return retType;
            }
        };
    }

}
