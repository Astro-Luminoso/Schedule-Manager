package dev.nbcsparta.assignment.schedulemanager.advisor;

import dev.nbcsparta.assignment.schedulemanager.exception.*;
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

    @ExceptionHandler(ClientNotAuthorisedException.class)
    public ResponseEntity<Void> handleClientNotAuthorisedException(ClientNotAuthorisedException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<Void> handleAuthorNotFoundException(AuthorNotFoundException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Void> handleDuplicateUserException(DuplicateUserException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Void> handleDuplicateUserException(CommentNotFoundException ex) {
        return ResponseEntity.status(ex.getStatus()).build();
    }
}
