package com.bangmaple.webflux.repositories;

import com.bangmaple.webflux.entities.AbstractEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@NoRepositoryBean
public interface CustomizedReactiveCrudRepository<T,ID>
        extends ReactiveCrudRepository<T, Object> {
    default <S extends T> Mono<S> persist(S entity) {
        AbstractEntity ae = (AbstractEntity) entity;
        return findById(Objects.requireNonNull(ae.getId()))
                .switchIfEmpty(Mono.defer(() -> {
            ae.setNewEntity(true);
            return save(entity);
        })).map(v -> {
            throw new IllegalArgumentException();
        });
    }

    //default <S extends AbstractEntity> Flux<S> persistAll(Iterable<S> entities) {
     //   entities.forEach(entity -> entity.setNewEntity(true));
      //  return saveAll(entities);
    //}
}
