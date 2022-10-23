package ru.practicum.shareit.exception;

/*
Класс передает сообщение, если возникло исключение
        */

public class AllOtherException extends RuntimeException {
    public AllOtherException(String message) {
        super(message);
    }
}