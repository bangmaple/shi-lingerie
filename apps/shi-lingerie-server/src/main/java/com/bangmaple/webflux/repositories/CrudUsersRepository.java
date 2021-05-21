package com.bangmaple.webflux.repositories;

import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.models.AddNewUserModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CrudUsersRepository extends ReactiveCrudRepository<Users, Integer> {

  @Query("INSERT INTO users(username, password, fullname, is_activated) VALUES (userModel.username, userModel.password, userModel.fullname, userModel.isActivated)")
  Mono<Users> addNewUser(AddNewUserModel userModel);
}
