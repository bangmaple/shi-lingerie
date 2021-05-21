package com.bangmaple.webflux.filter;

import com.bangmaple.webflux.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

  private final ReactiveAuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    System.out.println(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    return jwtUtil.resolveToken(exchange.getRequest()).flatMap((authToken) -> this.authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(authToken, authToken))
      .map((SecurityContextImpl::new)));
  }
}
