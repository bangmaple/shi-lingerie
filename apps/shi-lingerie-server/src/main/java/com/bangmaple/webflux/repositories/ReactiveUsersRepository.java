package com.bangmaple.webflux.repositories;

import com.bangmaple.webflux.models.AuthenticationResponse;
import com.bangmaple.webflux.entities.Users;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveUsersRepository extends ReactiveCrudRepository<Users, Integer> {
    @Query("SELECT u.id, u.username, u.fullname, u.password, u.is_activated, r.role_name FROM users u JOIN roles r ON u.role_id = r.id WHERE u.username = :username")
    Mono<Users> findByUsername(String username);

    @Query("SELECT u.id, u.username, u.fullname, u.is_activated, r.role_name FROM users u JOIN roles r ON r.id = u.role_id WHERE u.username = :username")
    Mono<AuthenticationResponse> findAuthenticationResponseByUsername(String username);

    @Query("INSERT INTO users(username, password, fullname, role_id, is_activated) VALUES (:username, :password, :fullname, 2, false)")
    Mono<Users> signUpAUser(String username, String password, String fullname);
}
