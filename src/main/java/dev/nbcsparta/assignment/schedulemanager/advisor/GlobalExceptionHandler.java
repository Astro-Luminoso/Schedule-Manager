package dev.nbcsparta.assignment.schedulemanager.advisor;

import dev.nbcsparta.assignment.schedulemanager.exception.EventNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.PasswordNotMatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<Void> handlePasswordNotMatchException(PasswordNotMatchException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Void> handleEventNotFoundException(EventNotFoundException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }
}
