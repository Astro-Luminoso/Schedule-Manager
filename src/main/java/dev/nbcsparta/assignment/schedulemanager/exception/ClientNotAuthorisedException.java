package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class ClientNotAuthorisedException extends RuntimeException {

    HttpStatus status;

    public ClientNotAuthorisedException(HttpStatus status, Long sessionUserId, Long authorId) {
        super(String.format("Client id %d is not authorized to access the resource of author id %d%n", sessionUserId, authorId));
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
