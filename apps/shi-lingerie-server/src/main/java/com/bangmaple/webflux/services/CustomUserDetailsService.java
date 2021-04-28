package com.bangmaple.webflux.services;

import com.bangmaple.webflux.repositories.UsersRepository;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    @SneakyThrows(UsernameNotFoundException.class)
    public UserDetails loadUserByUsername(String username) {
        return (UserDetails) usersRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .switchIfEmpty(Mono.error(() -> {
                    throw new UsernameNotFoundException("User not found with username: " + username);
                }));

    }

    @Transactional
    public UserDetails loadUserById(Integer id) {
        return (UserDetails) usersRepository.findById(id).map(UserPrincipal::new)
                .switchIfEmpty(Mono.error(() -> {
                    throw new UsernameNotFoundException("User not found with id: " + id);
        }));
    }
}
