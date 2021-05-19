package com.bangmaple.webflux.filter;

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

@Slf4j
public class JwtAuthenticationFilter implements WebFilter {
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  private Mono<String> resolveToken(ServerHttpRequest request) {
    return Mono.create((sink) -> {
      String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
        sink.success(bearerToken.substring(7));
      } else {
        sink.success();
      }
    });
  }

  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    System.out.println("hi");
    return resolveToken(exchange.getRequest()).flatMap((token) ->
      this.jwtUtil.validateToken(token).flatMap((isValidated) ->
        this.jwtUtil.getAuthentication(token).flatMap((authentication) ->
          chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder
            .withAuthentication(authentication)))))
      .switchIfEmpty(chain.filter(exchange));
  }
}
