package ru.practicum.shareit.exception;

public class InvalidStateException extends IllegalArgumentException {
    public InvalidStateException(String s) {
        super(s);
    }
}
