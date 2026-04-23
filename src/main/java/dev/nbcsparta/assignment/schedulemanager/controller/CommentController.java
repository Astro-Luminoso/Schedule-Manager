package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostNewComment;
import dev.nbcsparta.assignment.schedulemanager.dto.response.AllCommentsByEvent;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommentDetail;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDetail> createNewComment(
            @Valid @RequestBody PostNewComment reqBody,
            @SessionAttribute(name = "LOGIN_USER", required = false)SessionUser sessionUser
    ) {
        if (sessionUser == null) {
            throw new ClientNotAuthorisedException(HttpStatus.UNAUTHORIZED);
        }
        CommentDetail resBody = commentService.createComment(reqBody, sessionUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(resBody);
    }

    @GetMapping
    public ResponseEntity<AllCommentsByEvent> getCommentById(@RequestParam Long eventId) {
        AllCommentsByEvent resBody = commentService.getCommentById(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }
}
