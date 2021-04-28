package com.bangmaple.webflux.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {

    @Autowired
    private CustomUser

    private static final String[] ALLOWED_PATHS = {"/",
            "/favicon.ico",
            "/*.png",
            "/*.gif",
            "/*.svg",
            "/*.jpg",
            "/*.html",
            "/*.css",
            "/*.js",
            "/actuator/**"};

    private static final String ALLOWED_PATH = "/api/**";

    @Bean
    protected SecurityWebFilterChain configureSecurityWebFilterChain(ServerHttpSecurity http)  {
        return http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeExchange()
                .pathMatchers(ALLOWED_PATHS)
                .permitAll()
                .pathMatchers(ALLOWED_PATH)
                .permitAll()
                .and()
                .build();

    }
}
