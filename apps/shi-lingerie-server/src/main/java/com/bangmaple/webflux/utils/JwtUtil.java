package com.bangmaple.webflux.utils;

import com.bangmaple.webflux.services.UserPrincipal;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public static final String SECRET = "1234567890987654321";
    public static final Long expireTime = 86400000L;

    public Authentication getAuthentication(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object authoritiesClaim = claims.get("roles");
        Object username = claims.get("username");
        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ?
                AuthorityUtils.NO_AUTHORITIES : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
        UserPrincipal principal = new UserPrincipal(username.toString(), authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Claims getAllClaimsFromToken(String token) {
        return (Jwts.parser().setSigningKey(SECRET)).parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getAllClaimsFromToken(token).getSubject());
    }

    public String getUsernameFromToken(String token) {
        return Objects.toString(getAllClaimsFromToken(token).get("username"), "");
    }
    public ArrayList<String> getRolesFromToken(String token) {
        return (ArrayList<String>) getAllClaimsFromToken(token).get("roles");
    }
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return !isTokenExpired(token);
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
        return Mono.defer(() -> {
            Mono<Collection<? extends GrantedAuthority>> authorities = Mono.just(authentication.getAuthorities());
            String username = authentication.getName();
            Mono<Claims> claims = Mono.just(Jwts.claims().setSubject(username));
            return authorities.flatMap((auth) -> {
               if (!auth.isEmpty()) {
                   return claims.map(claimList -> {
                     claimList.put("roles", auth.stream().map(GrantedAuthority::getAuthority)
                               .collect(Collectors.joining(",")));
                     return claimList;
                   });
               }
               return Mono.error(NoSuchElementException::new);
            });
        });

    }

    public Mono<String> createToken(Authentication authentication) {
        return createCurrentOwnClaims(authentication).map(c -> {
            Date now = new Date();
            Date validity = new Date(now.getTime() + expireTime);
            return Jwts.builder()
                    .setClaims((Claims) c)
                    .setIssuedAt(now)
                    .setExpiration(validity)
                    .signWith(SignatureAlgorithm.HS256, SECRET)
                    .compact();
        });
    }

}
