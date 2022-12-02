package ru.practicum.shareit.validation.exception;


/*
Класс передает сообщение для всех ситуаций, если искомый объект не найден;
 */
public class NotFoundObjectException extends NullPointerException {
    public NotFoundObjectException(String error) {
        super(error);
    }
}
