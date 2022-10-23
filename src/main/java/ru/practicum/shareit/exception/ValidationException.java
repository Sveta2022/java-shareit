package ru.practicum.shareit.exception;

/*
Класс передает сообщение при ошибки валидации
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}