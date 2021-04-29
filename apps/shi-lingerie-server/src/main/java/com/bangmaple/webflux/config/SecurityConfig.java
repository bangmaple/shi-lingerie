package com.bangmaple.webflux.config;

import com.bangmaple.webflux.filter.JwtAuthenticationFilter;
import com.bangmaple.webflux.repositories.UsersRepository;
import com.bangmaple.webflux.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

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
                            .pathMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN"))
                .addFilterAt(new JwtAuthenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();

    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UsersRepository users) {
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
