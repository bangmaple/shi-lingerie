package com.bangmaple.webflux.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleDBException(DataIntegrityViolationException ex) {
      //  log.error(ex.getMessage(), ex);
        return Mono.just(ResponseEntity.badRequest()
                .body(new ExceptionResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage())));
    }

    @ExceptionHandler(EntityValidatingException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleEntityValidationException(EntityValidatingException ex) {
        //  log.error(ex.getMessage(), ex);
        return Mono.just(ResponseEntity.badRequest()
                .body(new ExceptionResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getErrors())));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInputMismatchException(NoSuchElementException ex) {
        //  log.error(ex.getMessage(), ex);
        return Mono.just(ResponseEntity.badRequest()
                .body(new ExceptionResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Cannot find the entity with the provided id.")));
    }
}
