package com.bangmaple.webflux.controllers;

import com.bangmaple.webflux.models.AuthenticationResponse;
import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.services.UsersService;
import com.bangmaple.webflux.utils.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService service;
    private final ValidatorUtil<Users> validatorUtil;

    @GetMapping
    public Mono<ResponseEntity<Flux<Users>>> getAll() {
        return Mono.just(ResponseEntity.ok(service.getAll()));
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Mono<ResponseEntity<Mono<Users>>> getUserById(@PathVariable("id") Integer id) {
        return Mono.just(ResponseEntity.ok(service.getUserById(Mono.just(id))));
    }

    /*
    *     @PostMapping
    public Mono<ResponseEntity<Mono<Users>>> addUser(@Valid @RequestBody Users user) {
        return Mono.just(user).map(u -> (validatorUtil.validate(UsersValidator.class, u)))
                .map(service::add).flatMap((u) -> Mono.just(ResponseEntity.ok(u)));
    }
    * */
    @PostMapping
    public Mono<ResponseEntity<Mono<Users>>> addUser(@RequestBody Mono<@Valid Users> user) {
        return Mono.just(user).map(service::add).flatMap((u) -> Mono.just(ResponseEntity.ok(u)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("permitAll()")
    public Mono<ResponseEntity<Mono<Void>>> deleteUserById(@PathVariable("id") Integer id) {
        return Mono.just(ResponseEntity.ok(service.deleteById(Mono.just(id))));
    }
}
