package com.bangmaple.webflux.config;

import com.bangmaple.webflux.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
  private final JwtUtil jwtUtil;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();
    System.out.println("tok: " +authToken);
    System.out.println(authentication);
    return jwtUtil.validateToken(authToken).map(isValidated -> isValidated)
      .flatMap((e) -> jwtUtil.getUsernameFromToken(authToken)
        .flatMap(username -> jwtUtil.getAllClaimsFromToken()
          .flatMap(claims -> this.jwtUtil.getAuthentication(authToken))));
  }
}


