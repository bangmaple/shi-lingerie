package com.bangmaple.webflux.utils;

import com.bangmaple.webflux.aop.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthFilterExceptionHandler {
  private final JSONParserUtil<ExceptionResponse> jsonParserUtil;

  public Mono<? extends Void> constructBadRequestHttpResponse(ServerWebExchange exchange, Throwable e) {
    var httpResponse = exchange.getResponse();
    httpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
    var data = jsonParserUtil.getJSONStringFromJavaObject(new ExceptionResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage(), "Invalid JWT Token"))
      .getBytes(StandardCharsets.UTF_8);
    DataBuffer buffer = httpResponse.bufferFactory().allocateBuffer(data.length);
    buffer.write(data);
    return httpResponse.writeAndFlushWith(Flux.just(Flux.just(buffer)));
  }
}
