package com.bangmaple.webflux.repositories;

import com.bangmaple.webflux.entities.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UsersRepository extends ReactiveCrudRepository<Users, Integer> {

}
