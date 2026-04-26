package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class AuthorNotFoundException extends CustomException {

    public AuthorNotFoundException(HttpStatus status) {
        super("Author not found", status);
    }
}
