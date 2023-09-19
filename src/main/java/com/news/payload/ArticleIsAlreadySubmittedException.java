package com.news.payload;

import org.springframework.http.HttpStatus;

public class ArticleIsAlreadySubmittedException extends RuntimeException{
  private HttpStatus status;
  private String message;

  public ArticleIsAlreadySubmittedException(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public ArticleIsAlreadySubmittedException(String message, HttpStatus status, String message1) {
    super(message);
    this.status = status;
    this.message = message1;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
