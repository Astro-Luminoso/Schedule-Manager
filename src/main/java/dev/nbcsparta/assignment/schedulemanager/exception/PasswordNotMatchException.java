package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends RuntimeException {

    private HttpStatus status;

    public PasswordNotMatchException(HttpStatus status) {
        super("Invalid Password");
        this.status = status;
    }

    public HttpStatus getStatus(){
        return this.status;
    }
}
