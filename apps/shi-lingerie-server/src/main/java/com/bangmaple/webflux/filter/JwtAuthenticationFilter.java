package com.bangmaple.webflux.filter;

import com.bangmaple.webflux.utils.JwtAuthFilterExceptionHandler;
import com.bangmaple.webflux.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter {
  private final JwtUtil jwtUtil;
  private final JwtAuthFilterExceptionHandler jwtAuthFilterExceptionHandler;


  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

    return this.jwtUtil.resolveToken(exchange.getRequest()).flatMap((token) ->
      this.jwtUtil.validateToken(token).flatMap((isValidated) ->
        this.jwtUtil.getAuthentication(token).flatMap((authentication) ->
          chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder
            .withAuthentication(authentication)))
      .switchIfEmpty(chain.filter(exchange))
      .onErrorResume(e -> jwtAuthFilterExceptionHandler
        .constructBadRequestHttpResponse(exchange, e)).timeout(Duration.ofMillis(10000000))));
  }
}
