package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.NewComment;
import dev.nbcsparta.assignment.schedulemanager.dto.response.AllCommentsByEvent;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommentDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Comment;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.exception.CommentNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final ClientService clientService;
    private final EventService eventService;

    public CommentService(
            CommentRepository commentRepository,
            ClientService clientService,
            EventService eventService
    ) {
        this.commentRepository = commentRepository;
        this.clientService = clientService;
        this.eventService = eventService;
    }

    public CommentDetail createComment(NewComment reqBody, long sessionId) {
        Event event = eventService.getEvent(reqBody.eventId());
        if(event.getAuthor().getId() != sessionId) {
            throw new ClientNotAuthorisedException(HttpStatus.FORBIDDEN, sessionId, event.getAuthor().getId());
        }

        Client author = clientService.getClient(sessionId);

        Comment comment = reqBody.toEntity(event, author);
        comment = commentRepository.save(comment);
        return CommentDetail.from(comment);
    }

    @Transactional(readOnly = true)
    public AllCommentsByEvent getCommentById(Long eventId) {
        eventService.getEvent(eventId); /* although the method is not used on purpose Will throw exception if eventId is not valid */
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return AllCommentsByEvent.from(comments);
    }

    public CommentDetail patchCommentById(Long commentId, NewComment reqBody, long sessionId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(HttpStatus.NOT_FOUND, commentId));
        if(comment.getAuthor().getId() != sessionId) {
            throw new ClientNotAuthorisedException(HttpStatus.FORBIDDEN, sessionId, comment.getAuthor().getId());
        }
        comment.updateContent(reqBody.content());
        return CommentDetail.from(comment);
    }

    public void deleteCommentById(Long commentId, long sessionId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(HttpStatus.NOT_FOUND, commentId));
        if (comment.getAuthor().getId() != sessionId) {
            throw new ClientNotAuthorisedException(HttpStatus.FORBIDDEN, sessionId, comment.getAuthor().getId());
        }
        commentRepository.delete(comment);
    }
}
