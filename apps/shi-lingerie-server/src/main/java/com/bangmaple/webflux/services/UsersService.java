package com.bangmaple.webflux.services;

import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
public class UsersService {

    private UsersRepository repo;

    public UsersService(UsersRepository repo) {
        this.repo = repo;
    }

    public Flux<Users> getAll() {
        return repo.findAll();
    }

    public Mono<Users> add(Users user) {
        return repo.save(user);
    }

    public Mono<Users> getUserById(Integer id) {
        return repo.findById(id).switchIfEmpty(Mono.error(NoSuchElementException::new));
    }

    public Mono<Users> updateUser(Integer id, Users user) {
        return repo.findById(id).switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap((u) -> (repo.save(user)));
    }
    public Mono<Void> deleteById(Integer id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap((user) -> repo.deleteById(user.getId()));
    }
}
