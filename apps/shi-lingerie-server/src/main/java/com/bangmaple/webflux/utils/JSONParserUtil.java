package com.bangmaple.webflux.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class JSONParserUtil<T> {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @SuppressWarnings("BlockingMethodInNonBlockingContext")
  @SneakyThrows(JsonProcessingException.class)
  public String getJSONStringFromJavaObject(T t) {
    return objectMapper.writeValueAsString(t);
  }
}
