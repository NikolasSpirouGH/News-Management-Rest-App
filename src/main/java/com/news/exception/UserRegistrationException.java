package com.news.exception;

public class UserRegistrationException extends RuntimeException {
    public UserRegistrationException(String message) {
        super(message);
    }

    public static class UserAlreadyExistsException extends UserRegistrationException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class PasswordNotConfirmedException extends UserRegistrationException {
        public PasswordNotConfirmedException(String message) {
            super(message);
        }
    }
}

