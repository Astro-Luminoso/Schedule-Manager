package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class AuthorNotFoundException extends RuntimeException {
    private HttpStatus status;

    public AuthorNotFoundException(HttpStatus status) {
        super("Author not found");
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
