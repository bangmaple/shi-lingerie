package com.bangmaple.webflux.filter;

import com.bangmaple.webflux.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
  private final JwtUtil jwtUtil;

  @Value("${jwt.token-type}")
  private String tokenType;

  private Mono<String> resolveToken(ServerHttpRequest request) {
    return Mono.create((sink) -> {
      String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenType)) {
        sink.success(bearerToken.substring(7));
      } else {
        sink.success();
      }
    });
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return resolveToken(exchange.getRequest()).flatMap((token) -> {
      if (StringUtils.hasText(token)) {
        this.jwtUtil.validateToken(token).map(isValidated -> {
          if (isValidated) {
            return this.jwtUtil.getAuthentication(token).map(authentication -> chain.filter(exchange)
              .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
          }
          return chain.filter(exchange);
        });
      }
      return chain.filter(exchange);
    }).switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
  }
}
