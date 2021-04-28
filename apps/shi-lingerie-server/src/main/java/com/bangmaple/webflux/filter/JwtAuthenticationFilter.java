package com.bangmaple.webflux.filter;

import com.bangmaple.webflux.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Pattern actuator_pattern = Pattern.compile("^/actuator.*");
        String path = exchange.getRequest().getPath().toString();
        if (actuator_pattern.matcher(path).matches()) {
            chain.filter(exchange);
            return Mono.empty();
        }

        String jwt = getJwtFromRequest(exchange.getRequest());

        try {
            if (jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtUtil.getEmailFromToken(jwt);
                ArrayList<String> roles = jwtUtil.getRolesFromToken(jwt);
                UserDetails userDetails = User.withUsername(email).password("").roles(String.join(",", roles)).build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(exchange.getRequest()));
                authentication.setDetails(exchange.getRequest());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (JwtException ex) {
            System.out.println("Invalid JWT signature");
           // logger.error("Invalid JWT signature");
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().set("Content-Type","application/json");
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(ex.getMessage().getBytes());
            exchange.getResponse().writeWith(Flux.just(buffer));
            return Mono.empty();
        }
        return chain.filter(exchange);
    }
}