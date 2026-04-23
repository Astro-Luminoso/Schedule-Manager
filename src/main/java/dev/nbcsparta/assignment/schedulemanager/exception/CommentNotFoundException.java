package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends RuntimeException {

    private final HttpStatus status;

    public CommentNotFoundException (HttpStatus status, long id) {
        super("Comment not found with id: " + id);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

