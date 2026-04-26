package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends CustomException {


    public PasswordNotMatchException(HttpStatus status) {
        super("Invalid Password", status);
    }
}
