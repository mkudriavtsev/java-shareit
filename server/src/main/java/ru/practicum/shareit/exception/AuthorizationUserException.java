package ru.practicum.shareit.exception;

public class AuthorizationUserException extends RuntimeException {
    public AuthorizationUserException(String message) {
        super(message);
    }
}
