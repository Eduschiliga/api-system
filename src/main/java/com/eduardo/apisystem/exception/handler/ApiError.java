package com.eduardo.apisystem.exception.handler;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;

  public ApiError(HttpStatus status, String message, String path) {
    this.timestamp = LocalDateTime.now();
    this.status = status.value();
    this.error = status.getReasonPhrase();
    this.message = message;
    this.path = path;
  }
}
