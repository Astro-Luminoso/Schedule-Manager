package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends CustomException {

    public DuplicateUserException(HttpStatus status) {
        super("Email Already Exists", status);
    }
}
