package com.bangmaple.webflux.config;

import com.bangmaple.webflux.filter.JwtAuthenticationFilter;
import com.bangmaple.webflux.filter.SecurityContextRepository;
import com.bangmaple.webflux.repositories.AuthenticationUsersRepository;
import com.bangmaple.webflux.utils.JwtAuthFilterExceptionHandler;
import com.bangmaple.webflux.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
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
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(proxyTargetClass = true)
/*@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        proxyTargetClass = true,
       prePostEnabled = true
)*/
public class SecurityConfig implements WebFluxConfigurer {

  @Value("${security.api}")
  private String API;

  @Value("${security.api.users}")
  private String USERS;

  @Value("${security.api.authentication-paths}")
  private String[] AUTHENTICATION_PATHS;


  @Value("${security.allowed-paths}")
  private String[] ALLOWED_PATHS;


  @Bean
  //https://github.com/ard333/spring-boot-webflux-jjwt/tree/master/src/main/java/com/ard333/springbootwebfluxjjwt/security
  protected SecurityWebFilterChain configureSecurityWebFilterChain(ServerHttpSecurity http,
                                                                   ReactiveAuthenticationManager
                                                                     reactiveAuthenticationManager,
                                                                   AuthenticationManager
                                                                     authManager,
                                                                   SecurityContextRepository
                                                                       securityContextRepository,
                                                                   JwtUtil jwtUtil,
                                                                   JwtAuthFilterExceptionHandler jwtAuthFilterExceptionHandler) {
    return http
      //.cors(ServerHttpSecurity.CorsSpec::disable)
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .exceptionHandling()
      .authenticationEntryPoint((swe, e) ->
        Mono.fromRunnable(() ->
          swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
      .accessDeniedHandler((swe, e) ->
        Mono.fromRunnable(() ->
          swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
      .and()
      .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
      .logout(ServerHttpSecurity.LogoutSpec::disable)
      .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
      .authenticationManager(authManager)
     // .authenticationManager(reactiveAuthenticationManager)
     // .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, jwtAuthFilterExceptionHandler),
     //   SecurityWebFiltersOrder.HTTP_BASIC)
      .securityContextRepository(securityContextRepository)
      .authorizeExchange()
      .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
      .pathMatchers(HttpMethod.POST, AUTHENTICATION_PATHS).permitAll()
      .pathMatchers(HttpMethod.GET, ALLOWED_PATHS).permitAll()
      .pathMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")
      .and()
      //.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
     /* .authorizeExchange((it) -> it
          .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
          .pathMatchers(HttpMethod.POST, AUTHENTICATION_PATHS).permitAll()
          .pathMatchers(HttpMethod.GET, ALLOWED_PATHS).permitAll()
          .pathMatchers(HttpMethod.POST, "/api/v1/users").hasRole("ADMIN")*/
        // .pathMatchers(HttpMethod.DELETE, API + USERS +"/**").hasRole("ADMIN")
    //  )
    .build();

  }

  @Bean
  public ReactiveUserDetailsService userDetailsService(AuthenticationUsersRepository users) {
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
