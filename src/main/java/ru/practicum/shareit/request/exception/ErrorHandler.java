package ru.practicum.shareit.request.exception;

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
                Map.of("error", v.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundObjectException n) {
        log.warn("404 {}", n.getMessage(), n);
        return new ResponseEntity<>(
                Map.of("error", n.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleAllOther(final AllOtherException a) {
        log.warn("409 {}", a.getMessage(), a);
        return new ResponseEntity<>(
                Map.of("error", a.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleAllIlligal(final NoligalArgumentException nL) {
        log.warn("500 {}", nL.getMessage(), nL);
        return new ResponseEntity<>(
                Map.of("error", nL.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}