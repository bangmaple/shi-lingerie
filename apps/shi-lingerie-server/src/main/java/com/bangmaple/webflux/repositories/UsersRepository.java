package com.bangmaple.webflux.repositories;

import com.bangmaple.webflux.entities.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsersRepository extends ReactiveCrudRepository<Users, Integer> {
    Mono<Users> findByUsername(String username);
}
