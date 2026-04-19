package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends RuntimeException {
    HttpStatus status;
    public DuplicateUserException(HttpStatus status) {
        super("이미 존재하는 이메일입니다.");
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}
