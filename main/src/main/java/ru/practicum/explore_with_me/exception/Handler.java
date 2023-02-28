package ru.practicum.explore_with_me.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@RestControllerAdvice("ru.practicum.explore_with_me.controller")
@Slf4j
public class Handler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleException(Exception e) {
        return new ErrorBody(HttpStatus.BAD_REQUEST, List.of("Неизвестная ошибка"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorBody handleThrowableException(Throwable e) {
        return new ErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Неизвестная ошибка"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBody handleNotFound(NotFoundException e) {
        return new ErrorBody(e.getStatus(), List.of("Не найдено"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorBody handleConflict(ConflictException e) {
        return new ErrorBody(e.getStatus(), List.of("Неуникальное значение"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleBadRequest(InvalidDataException e) {
        return new ErrorBody(e.getStatus(), List.of("Ошибка при добавлении или обновлении"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorBody handleConstraintViolation(ConstraintViolationException e) {
        return new ErrorBody(HttpStatus.CONFLICT, List.of("constr violation"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleArgumentNotValid(MethodArgumentNotValidException e) {
        return new ErrorBody(HttpStatus.BAD_REQUEST, List.of("Ошибка данных"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorBody handleIntegrityViolation(DataIntegrityViolationException e) {
        return new ErrorBody(HttpStatus.CONFLICT, List.of("Ошибка целостности данных"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody handleIllegalArgument(IllegalArgumentException e) {
        return new ErrorBody(HttpStatus.BAD_REQUEST, List.of("Illegal Argument"));
    }
}
