package com.bangmaple.webflux.filter;

import com.bangmaple.webflux.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
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


    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = resolveToken(exchange.getRequest());
        if (StringUtils.hasText(token) && this.jwtUtil.validateToken(token)) {
            Authentication authentication = this.jwtUtil.getAuthentication(token);
            System.err.println(jwtUtil.getAllClaimsFromToken(token));
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return chain.filter(exchange);
    }

   /* public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Pattern actuator_pattern = Pattern.compile("^/as.*");
        String path = exchange.getRequest().getPath().toString();
        if (actuator_pattern.matcher(path).matches()) {
            return chain.filter(exchange);
        }

        String jwt = getJwtFromRequest(exchange.getRequest());
        System.err.println(jwt);
        try {
            if (jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.getUsernameFromToken(jwt);
                ArrayList<String> roles = jwtUtil.getRolesFromToken(jwt);
                UserDetails userDetails = User.withUsername(username).password("").roles(String.join(",", roles)).build();
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

    */
}