package com.bangmaple.webflux.utils;

import com.bangmaple.webflux.services.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    public String SECRET;

    @Value("${jwt.expiration-time}")
    public Long expireTime;

    private static final String JWT_CLAIMS_ROLES = "roles";

    private Jws<Claims> claimsInstance;

    public Mono<Authentication> getAuthentication(String token) {
        return getAllClaimsFromToken().map((claims) -> {
            String authoritiesClaim = Objects.toString(claims.get(JWT_CLAIMS_ROLES), "");
            String username = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils
              .commaSeparatedStringToAuthorityList(authoritiesClaim);
            UserPrincipal principal = new UserPrincipal(username, authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        });

    }

    public Mono<Claims> getAllClaimsFromToken() {
        return Mono.create((sink) -> sink.success(claimsInstance.getBody()));
    }

    //Deprecated
    public Mono<Long> getUserIdFromToken() {
        return getAllClaimsFromToken().map(claims -> Long.parseLong(claims.getSubject()));
    }

    public Mono<String> getUsernameFromToken(String token) {
        return getAllClaimsFromToken().map(claims -> Objects
          .toString(claims.getSubject(), ""));
    }

    //Deprecated
    public Flux<String> getRolesFromToken() {
        return  getAllClaimsFromToken()
                .flatMapMany(claims -> Flux.fromIterable((ArrayList<String>)claims
                  .get(JWT_CLAIMS_ROLES)));
    }

    public Mono<Date> getExpirationDateFromToken() {
        return getAllClaimsFromToken().map(Claims::getExpiration);
    }

    private Mono<Boolean> isTokenExpired() {
        return getExpirationDateFromToken()
                .map(expirationDate -> expirationDate.before(new Date()));
    }


    public Mono<Boolean> validateToken(String token) {
          claimsInstance = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
          return isTokenExpired().flatMap(isExpired -> Mono.empty());
    }

    public Mono<Claims> createCurrentOwnClaims(Authentication authentication) {
        return Mono.just(authentication.getAuthorities())
          .map((auth) -> {
            String username = authentication.getName();
            Claims claims = Jwts.claims().setSubject(username);
            if (!auth.isEmpty()) {
              claims.put(JWT_CLAIMS_ROLES, auth.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
            }
            return claims;
          });
    }

    public Mono<String> createToken(Authentication authentication) {
        return createCurrentOwnClaims(authentication).map(claims -> {
            Date now = new Date();
            Date validity = new Date(now.getTime() + expireTime);
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(SignatureAlgorithm.HS256, SECRET)
                    .compact();
        });
    }

  public Mono<String> resolveToken(ServerHttpRequest request) {
    return Mono.create((sink) -> {
      String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
        sink.success(bearerToken.substring(7));
      } else {
        sink.success();
      }
    });
  }

}
