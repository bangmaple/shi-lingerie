package com.bangmaple.webflux.config;

import com.bangmaple.webflux.filter.JwtAuthenticationFilter;
import com.bangmaple.webflux.repositories.ReactiveUsersRepository;
import com.bangmaple.webflux.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
/*@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        proxyTargetClass = true,
       prePostEnabled = true
)*/
public class SecurityConfig implements WebFluxConfigurer {

    private final JwtUtil jwtUtil;

    private static final String API = "/api/v1/";
    private static final String USERS = "users/";

    private static final String[] AUTHENTICATION_PATHS = {
            API + USERS + "signin",
            API + USERS + "signup",
            API + USERS + "forgotpwd"
    };


    private static final String[] ALLOWED_PATHS = {"/",
            "/favicon.ico",
            "/*.png",
            "/*.gif",
            "/*.svg",
            "/*.jpg",
            "/*.html",
            "/*.css",
            "/*.js",
            "/actuator/**"};


    @Bean
    protected SecurityWebFilterChain configureSecurityWebFilterChain(ServerHttpSecurity http,
                                                                     ReactiveAuthenticationManager
                                                                             reactiveAuthenticationManager)  {
        return http
                .cors()
                .and()
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange((it) -> it
                            .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .pathMatchers(HttpMethod.POST, AUTHENTICATION_PATHS).permitAll()
                            .pathMatchers(HttpMethod.GET, ALLOWED_PATHS).permitAll()
                            .pathMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN"))

                .addFilterAt(new JwtAuthenticationFilter(jwtUtil), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();

    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(ReactiveUsersRepository users) {
        return username -> users.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                                .password(u.getPassword())
                .authorities(u.getRole())
                .accountExpired(!u.isActivated())
                .credentialsExpired(!u.isActivated())
                .disabled(!u.isActivated())
                .accountLocked(!u.isActivated())
                .build());
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }
}
