package dev.nbcsparta.assignment.schedulemanager.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends CustomException {


    public CommentNotFoundException (HttpStatus status, long id) {
        super("Comment not found with id: " + id, status);
    }
}

