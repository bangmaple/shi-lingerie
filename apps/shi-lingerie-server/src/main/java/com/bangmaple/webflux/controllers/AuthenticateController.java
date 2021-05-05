package com.bangmaple.webflux.controllers;

import com.bangmaple.webflux.models.AuthenticationRequest;
import com.bangmaple.webflux.models.SignUpModel;
import com.bangmaple.webflux.services.AuthenticateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class AuthenticateController {
    private final AuthenticateService service;

    @PostMapping("forgotpwd")
    public Mono<ResponseEntity<?>> forgotPwd() {
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("signout")
    public Mono<ResponseEntity<Mono<?>>> signOut() {
        return Mono.empty();
    }

    @PostMapping("signin")
    public Mono<ResponseEntity<?>> signIn(@RequestBody Mono<@Valid AuthenticationRequest> user) {
        return service.signin(user).flatMap(map -> Mono.just(ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, (String) map.get(HttpHeaders.AUTHORIZATION))
                .body(map.get("USER"))));
    }

    @PostMapping("signup")
    public Mono<ResponseEntity<Mono<?>>> signUp(@RequestBody Mono<@Valid SignUpModel> user) {
        return Mono.just(ResponseEntity.ok(service.signup(user)));
    }
}
