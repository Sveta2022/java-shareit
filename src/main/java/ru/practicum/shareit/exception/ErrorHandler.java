package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class,
        ItemRequestController.class})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final ValidationException v) {
        log.warn("400 {}", v.getMessage(), v);
        return new ResponseEntity<>(
                Map.of("Validation is not correct ", v.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundObjectException n) {
        log.warn("404 {}", n.getMessage(), n);
        return new ResponseEntity<>(
                Map.of("Object is not found ", n.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleAllOther(final AllOtherException a) {
        log.warn("500 {}", a.getMessage(), a);
        return new ResponseEntity<>(
                Map.of("Please, attention ", a.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
//    @ExceptionHandler
//    public ResponseEntity<Map<String, String>> handleAllGlobalException(final Throwable t) {
//        log.warn("500 {}", t.getMessage(), t);
//        return new ResponseEntity<>(
//                Map.of("Please, attention! Serious mistake ", t.getMessage()),
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}