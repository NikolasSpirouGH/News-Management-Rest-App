package com.news.exception;

public class InvalidUserNameException extends RuntimeException{

    public InvalidUserNameException(String message) {
        super(message);
    }
}
