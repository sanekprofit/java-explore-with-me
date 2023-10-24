package ru.practicum.exceptions;

public class BadParamException extends RuntimeException {
    public BadParamException(String s) {
        super(s);
    }
}
