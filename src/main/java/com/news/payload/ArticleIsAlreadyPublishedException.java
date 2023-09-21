package com.news.payload;

import org.springframework.http.HttpStatus;

public class ArticleIsAlreadyPublishedException extends RuntimeException{
  private HttpStatus status;
  private String message;

  public ArticleIsAlreadyPublishedException(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public ArticleIsAlreadyPublishedException(String message, HttpStatus status, String message1) {
    super(message);
    this.status = status;
    this.message = message1;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
