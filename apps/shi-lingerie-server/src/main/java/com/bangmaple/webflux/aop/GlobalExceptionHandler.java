package com.bangmaple.webflux.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.codec.DecodingException;
import org.springframework.dao.DataAccessResourceFailureException;
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
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
@PropertySource("classpath:global-exception-en.properties")
public class GlobalExceptionHandler {

  @ExceptionHandler(DataAccessResourceFailureException.class)
  public Mono<ResponseEntity<ExceptionResponse>> handleDatabaseConnectionFailure(DataAccessResourceFailureException e) {
    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.SERVICE_UNAVAILABLE.value(),
      "One or more services is down. Please comeback later!", "Service Unavailable")));

  }

    @ExceptionHandler(LockedException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleUserAccountLockedException(LockedException ex) {
        return Mono.just(ResponseEntity
        .badRequest().body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
            ACCOUNT_IS_LOCKED, "")));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidUsernamePasswordException(BadCredentialsException ex) {
        return Mono.just(ResponseEntity
                .badRequest().body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        INVALID_USERNAME_PASSWORD, ex.getMessage())));
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
                .body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage())));
    }

    @ExceptionHandler(EntityValidatingException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleEntityValidationException(EntityValidatingException ex) {
        //  log.error(ex.getMessage(), ex);
        return Mono.just(ResponseEntity.badRequest()
                .body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getErrors())));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInputMismatchException(NoSuchElementException ex) {
        //  log.error(ex.getMessage(), ex);
        return Mono.just(ResponseEntity.badRequest()
                .body(new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(), ENTITY_NOT_EXISTED_WITH_ID)));
    }

    @ExceptionHandler({DecodingException.class, ServerWebInputException.class})
    public Mono<ResponseEntity<ExceptionResponse>> handleInvalidIncomingRequestData(Exception ex) {
        return Mono.just(ResponseEntity
                .badRequest().body((new ExceptionResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        INVALID_REQUESTED_DATA, INVALID_DATA))));
    }

  @Value("${entity-not-existed-with-id}")
  private String ENTITY_NOT_EXISTED_WITH_ID;

  @Value("${invalid-requested-data}")
  private String INVALID_REQUESTED_DATA;

  @Value("${account-is-locked}")
  private String ACCOUNT_IS_LOCKED;

  @Value("${invalid-username-password}")
  private String INVALID_USERNAME_PASSWORD;

  @Value("${invalid-data}")
  private String INVALID_DATA;
}
