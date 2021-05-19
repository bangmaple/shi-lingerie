package com.bangmaple.webflux.utils;

import com.bangmaple.webflux.services.UserPrincipal;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
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
        return getAllClaimsFromToken(token).map((claims) -> {
            String authoritiesClaim = Objects.toString(claims.get(JWT_CLAIMS_ROLES), "");
            String username = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils
              .commaSeparatedStringToAuthorityList(authoritiesClaim);
            UserPrincipal principal = new UserPrincipal(username, authorities);
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        });

    }

    public Mono<Claims> getAllClaimsFromToken(String token) {
        return Mono.just(claimsInstance.getBody());
    }

    //Deprecated
    public Mono<Long> getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).map(claims -> Long.parseLong(claims.getSubject()));
    }

    public Mono<String> getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).map(claims -> Objects
          .toString(claims.getSubject(), ""));
    }

    //Deprecated
    public Flux<String> getRolesFromToken(String token) {
        return  getAllClaimsFromToken(token)
                .flatMapMany(claims -> Flux.fromIterable((ArrayList<String>)claims
                  .get(JWT_CLAIMS_ROLES)));
    }

    public Mono<Date> getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).map(Claims::getExpiration);
    }

    private Mono<Boolean> isTokenExpired(String token) {
        return getExpirationDateFromToken(token)
                .map(expirationDate -> expirationDate.before(new Date()));
    }


    public Mono<Boolean> validateToken(String token) {
        try {
          claimsInstance = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return isTokenExpired(token).map(isExpired -> !isExpired);
        } catch (SignatureException ex) {
            throw new JwtException("Invalid JWT signature", ex);
        } catch (MalformedJwtException ex) {
            throw new JwtException("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            throw new JwtException("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            throw new JwtException("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            throw new JwtException("JWT claims is empty.", ex);
        }
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

}
