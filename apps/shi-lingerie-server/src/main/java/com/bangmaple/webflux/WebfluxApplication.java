package com.bangmaple.webflux;

import com.bangmaple.webflux.repositories.CustomizedReactiveCrudRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
//@EnableR2dbcRepositories({"CustomizedReactiveCrudRepository.class"})
public class WebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }

    @Bean
    public PasswordEncoder enableBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
