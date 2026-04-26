package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class ClientNotAuthorisedException extends CustomException {

    public ClientNotAuthorisedException(HttpStatus status, Long sessionUserId, Long targetId) {
        super(String.format("Client id %d is not authorized to access the resource of author id %d%n", sessionUserId, targetId), status);
    }

    public ClientNotAuthorisedException(HttpStatus status) {
        super("Unauthorised access to the resource", status);
    }
}
