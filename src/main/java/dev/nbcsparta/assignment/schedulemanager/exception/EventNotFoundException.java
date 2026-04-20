package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class EventNotFoundException extends RuntimeException {

    HttpStatus status;

    public EventNotFoundException(HttpStatus status, Long eventId) {
        super(String.format("Event id %d not found%n", eventId));
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
