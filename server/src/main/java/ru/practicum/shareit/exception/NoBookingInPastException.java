package ru.practicum.shareit.exception;

public class NoBookingInPastException extends RuntimeException {
    public NoBookingInPastException(String message) {
        super(message);
    }
}
