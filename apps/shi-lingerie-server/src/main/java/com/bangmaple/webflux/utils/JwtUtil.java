package com.bangmaple.webflux.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    public static final String SECRET = "1234567890987654321";
    public static final Long expireTime = 86400000L;

    public Claims getAllClaimsFromToken(String token) {
        return (Jwts.parser().setSigningKey(SECRET)).parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getAllClaimsFromToken(token).getSubject());
    }

    public String getEmailFromToken(String token) {
        return Objects.toString(getAllClaimsFromToken(token).get("email"), "");
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

}
