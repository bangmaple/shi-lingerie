package com.bangmaple.webflux.services;

import com.bangmaple.webflux.entities.AuthenticationModel;
import com.bangmaple.webflux.entities.UserSignupModel;
import com.bangmaple.webflux.entities.Users;
import com.bangmaple.webflux.repositories.UsersRepository;
import com.bangmaple.webflux.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class UsersService {

    private UsersRepository repo;
    private PasswordEncoder passwordEncoder;

    private ReactiveAuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    public UsersService(UsersRepository repo,
                        PasswordEncoder passwordEncoder,
                        ReactiveAuthenticationManager authenticationManager,
                        JwtUtil jwtUtil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public Flux<Users> getAll() {
        return repo.findAll();
    }

    public Mono<Users> add(Mono<Users> user) {
        return user.flatMap(u -> repo.save(u)).switchIfEmpty(Mono.error(NullPointerException::new));
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
                .flatMap((u) -> (repo.save(u)));
    }

    public Mono<Void> deleteById(Mono<Integer> id) {
        return repo.existsById(id).flatMap((flag) -> !flag
                ?  Mono.error(NoSuchElementException::new)
                : Mono.empty());
    }

    public Mono<?> signin(Mono<AuthenticationModel> user) {
        return user.flatMap(u ->  this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(), passwordEncoder.encode(u.getPassword())))
                    .map(jwtUtil::createToken))
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("access_token", jwt);
                    return new ResponseToken(tokenBody,httpHeaders, HttpStatus.OK);
                });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ResponseToken {
        private Map<String, String> tokenBody;
        private HttpHeaders header;
        private HttpStatus status;

    }

    public Mono<Users> signup(Mono<UserSignupModel> user) {
        return user.flatMap(u -> repo.save(new Users(u.getUsername(), passwordEncoder.encode(u.getPassword()), u.getFullname())));
    }


}
