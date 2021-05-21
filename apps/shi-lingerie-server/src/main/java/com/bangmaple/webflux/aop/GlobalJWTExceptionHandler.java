package com.bangmaple.webflux.aop;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class GlobalJWTExceptionHandler {
  @ExceptionHandler(JwtException.class)
  public Mono<ResponseEntity<ExceptionResponse>> handleJwtException(JwtException e) {
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
        e.getMessage(), "Invalid JWT Token")));
  }
}
