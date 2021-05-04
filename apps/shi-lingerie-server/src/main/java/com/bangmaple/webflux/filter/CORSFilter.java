package com.bangmaple.webflux.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class CORSFilter implements WebFluxConfigurer {

    private static final long MAX_AGE_SECS = 3600;
    private static final String[] HTTP_METHODS_ALLOWED = {"HEAD", "OPTIONS", "GET",
            "POST", "PUT", "PATCH", "DELETE", "UPDATE"};

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods(HTTP_METHODS_ALLOWED)
                .allowedHeaders("*")
                .maxAge(MAX_AGE_SECS);
    }

}
