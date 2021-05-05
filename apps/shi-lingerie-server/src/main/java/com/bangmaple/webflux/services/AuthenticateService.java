package com.bangmaple.webflux.services;

import com.bangmaple.webflux.models.AuthenticationRequest;
import com.bangmaple.webflux.models.AuthenticationResponse;
import com.bangmaple.webflux.models.SignUpModel;
import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.repositories.ReactiveUsersRepository;
import com.bangmaple.webflux.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private final ReactiveUsersRepository repo;
    private final JwtUtil jwtUtil;

    public Mono<Map<String, Object>> signin(Mono<AuthenticationRequest> requestedAuthenticatingUser) {
        return requestedAuthenticatingUser.flatMap(authenticatingUser -> this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticatingUser.getUsername(),
                        authenticatingUser.getPassword()))
                .flatMap((authenticatedUser ->
                        repo.findAuthenticationResponseByUsername(((User) authenticatedUser
                                .getPrincipal()).getUsername())
                               // .flatMap(this::setSignedInStatus)
                                .flatMap(userDetail -> jwtUtil
                                        .createToken(authenticatedUser).flatMap(jwtToken -> Mono.just(Map
                                                .of(HttpHeaders.AUTHORIZATION,
                                                    "Bearer " + jwtToken,
                                                    "USER", userDetail)))))));
    }

    public Mono<Users> signup(Mono<SignUpModel> user) {
        return user.flatMap(u -> repo.signUpAUser(u.getUsername(),
                passwordEncoder.encode(u.getPassword()), u.getFullname()));
    }

    public Mono<AuthenticationResponse> setSignedInStatus(AuthenticationResponse res) {
        return repo.changeToSignedInStatus(res.getUsername()).map(flag -> res);
    }

    public Mono<AuthenticationResponse> setSignedOutStatus(AuthenticationResponse res) {
         return repo.changeToSignedOutStatus(res.getUsername()).map(flag -> res);
    }
}
