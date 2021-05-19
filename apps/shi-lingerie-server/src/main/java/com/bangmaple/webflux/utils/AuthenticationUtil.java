package com.bangmaple.webflux.utils;

import com.bangmaple.webflux.models.AuthenticationRequest;
import com.bangmaple.webflux.models.AuthenticationResponse;
import com.bangmaple.webflux.repositories.ReactiveUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

  private final ReactiveUsersRepository repo;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final ReactiveAuthenticationManager authenticationManager;

  public Mono<Authentication> getAuthentication(AuthenticationRequest authenticatingUser) {
    return this.authenticationManager
      .authenticate(getAuthenticationToken(authenticatingUser));
  }

  public Mono<AuthenticationResponse>
      getUsernameFromAuthenticatedUser(Authentication authenticatedUser) {
    return repo.findAuthenticationResponseByUsername(((User) authenticatedUser
      .getPrincipal()).getUsername());
  }

  public Mono<Map<String, Object>> getResponseAuthenticatedObject(Authentication authenticatedUser,
                                                                  AuthenticationResponse userDetail) {
    return jwtUtil
      .createToken(authenticatedUser).map(jwtToken -> Map
        .of(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken, "USER", userDetail));
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(AuthenticationRequest
                                                                      authenticatingUser) {
    return new UsernamePasswordAuthenticationToken(authenticatingUser.getUsername(),
      authenticatingUser.getPassword());
  }

  public String getEncodedPassword(String password) {
    return passwordEncoder.encode(password);
  }
}
