package ru.practicum.shareit.request.exception;

/*
Класс передает сообщение, если возникло исключение
        */

public class AllOtherException extends RuntimeException {
    public AllOtherException(String error) {
        super(error);
    }
}