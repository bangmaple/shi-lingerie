package com.bangmaple.webflux.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class JWTUtilsAspect {
  @AfterThrowing(pointcut = "execution(* com.bangmaple.webflux.utils.JwtUtil.*(..))",
    throwing = "e")
  public void handleJWTException(JoinPoint thisJoinPoint, Throwable e) {
    log.error(e.getMessage());
  }
}
