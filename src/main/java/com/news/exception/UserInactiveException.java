package com.news.exception;

public class UserInactiveException extends RuntimeException{

    public UserInactiveException(String message) {
        super(message);
    }
}
