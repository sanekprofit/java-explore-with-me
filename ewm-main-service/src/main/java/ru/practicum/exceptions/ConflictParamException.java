package ru.practicum.exceptions;

public class ConflictParamException extends RuntimeException {
    public ConflictParamException(String s) {
        super(s);
    }
}