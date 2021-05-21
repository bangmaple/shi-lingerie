package com.bangmaple.webflux.services;


import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.models.AddNewUserModel;
import com.bangmaple.webflux.repositories.AuthenticationUsersRepository;
import com.bangmaple.webflux.repositories.CrudUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final CrudUsersRepository repo;

    public Flux<Users> getAll() {
        return repo.findAll();
    }

    public Mono<Users> add(Mono<AddNewUserModel> user) {
        return user.flatMap(repo::addNewUser).switchIfEmpty(Mono.error(NullPointerException::new));
    }

    public Mono<Users> getUserById(Mono<Integer> id) {
        return repo.findById(id).switchIfEmpty(Mono.error(NoSuchElementException::new));
    }

    public Mono<Users> mapDTOToEntity(Mono<Users> monoDTO) {
        return monoDTO.switchIfEmpty(Mono.error(NullPointerException::new))
                .map(dto -> {
                    Mono<Users> monoEntity = Mono.just(new Users());
                    monoEntity.map(entity -> {
                        if (Objects.isNull(dto)) {
                            return Mono.error(NullPointerException::new);
                        }
                        entity.setUsername(dto.getUsername());
                        entity.setPassword(dto.getPassword());
                        entity.setFullname(dto.getFullname());
                        entity.setRole(dto.getRole());
                        return monoEntity;
                    });
                    return dto;
                });
    }

    public Mono<Users> updateUser(Mono<Integer> id, Mono<Users> user) {
        return repo.findById(id).switchIfEmpty(Mono.error(NoSuchElementException::new))
                .flatMap((u) -> mapDTOToEntity(Mono.just(u)))
                .flatMap(repo::save);
    }

    public Mono<Void> deleteById(Mono<Integer> id) {
        return repo.existsById(id).flatMap((flag) -> !flag
                ? Mono.error(NoSuchElementException::new)
                : repo.deleteById(id));
    }

}
