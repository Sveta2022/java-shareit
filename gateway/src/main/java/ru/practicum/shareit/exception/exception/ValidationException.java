package ru.practicum.shareit.validation.exception;

/*
Класс передает сообщение при ошибки валидации
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String error) {
        super(error);
    }
}