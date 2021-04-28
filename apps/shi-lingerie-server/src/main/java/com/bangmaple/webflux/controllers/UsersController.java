package com.bangmaple.webflux.controllers;

import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.services.UsersService;
import com.bangmaple.webflux.utils.ValidatorUtil;
import com.bangmaple.webflux.validator.UsersValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/users")
public class UsersController {

    private final UsersService service;
    private final ValidatorUtil<Users> validatorUtil;

    public UsersController(UsersService service,
                           ValidatorUtil validatorUtil) {
        this.service = service;
        this.validatorUtil = validatorUtil;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Users>>> getAll() {
        return Mono.just(ResponseEntity.ok(service.getAll()));
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Mono<Users>>> getUserById(@PathVariable("id") Integer id) {
        return Mono.just(ResponseEntity.ok(service.getUserById(id)));
    }

    @PostMapping
    public Mono<ResponseEntity<Mono<Users>>> addUser(@RequestBody Users user) {
        return Mono.just(user).map(u -> (validatorUtil.validate(UsersValidator.class, u)))
                .map(service::add).flatMap((u) -> Mono.just(ResponseEntity.ok(u)));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Mono<ResponseEntity<Mono<Void>>> deleteUserById(@PathVariable("id") Integer id) {
        return Mono.just(ResponseEntity.ok(service.deleteById(id)));
    }
}
