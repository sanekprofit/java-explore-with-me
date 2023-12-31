package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParameterException(MissingServletRequestParameterException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadParamException(BadParamException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictParamException(ConflictParamException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePSQLException(PSQLException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleJdbcSQLIntegrityConstraint(JdbcSQLIntegrityConstraintViolationException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrity(DataIntegrityViolationException e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        logError(e);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getClass().toString(),
                e.getMessage(),
                LocalDateTime.now());
    }

    private void logError(Throwable e) {
        log.error(String.format("%s throw an exception. message: %s", e.getClass().toString(), e.getMessage()));
    }
}
