package com.bangmaple.webflux.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class CORSFilter implements WebFluxConfigurer {

  @Value("${cors.max-age}")
  private Integer MAX_AGE_SECS;

  @Value("${cors.allowed-http-methods}")
  private String[] HTTP_METHODS_ALLOWED;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods(HTTP_METHODS_ALLOWED)
      .allowedHeaders("*")
      .maxAge(MAX_AGE_SECS);
  }

}
