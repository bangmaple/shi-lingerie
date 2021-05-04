package com.bangmaple.webflux.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleUserAccountLockedException(LockedException ex) {
        return Mono.just(ResponseEntity
        .badRequest().body(new ExceptionResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Your account is locked! Please contact to Administrator for more details.",
                        "")));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidUsernamePasswordException(BadCredentialsException ex) {
        return Mono.just(ResponseEntity
                .badRequest().body(new ExceptionResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid username or password. Please try again later.",
                        ex.getMessage())));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, WebExchangeBindException.class})
    public Mono<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Mono<Map<String, String>> errors = Mono.just(new HashMap<>());
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.subscribe(map -> map.put(fieldName, errorMessage));
        });
        return errors;
    }

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

    @ExceptionHandler({DecodingException.class, ServerWebInputException.class})
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidIncomingRequestData(Exception ex) {
        return Mono.just(ResponseEntity
                .badRequest().body((new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        "Invalid requested data. Please try again before sending.",
                        "Invalid data."))));
    }
}
