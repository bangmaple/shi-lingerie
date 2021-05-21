package com.bangmaple.webflux.aop;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ExceptionResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private List<ObjectError> validatingErrors;

    public ExceptionResponse(LocalDateTime timestamp, Integer status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ExceptionResponse(LocalDateTime timestamp, Integer status, String error, List<ObjectError> validatingErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.validatingErrors = validatingErrors;
    }

}
