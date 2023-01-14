package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownRequestState(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String error;
    }
}
