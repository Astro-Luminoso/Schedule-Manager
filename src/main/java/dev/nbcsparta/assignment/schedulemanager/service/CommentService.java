package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostNewComment;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonCommentDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Comment;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CommonCommentDetail createComment(PostNewComment reqBody, long sessionId) {
        if(reqBody.ClientId() != sessionId) {
            throw new ClientNotAuthorisedException(HttpStatus.FORBIDDEN, sessionId, reqBody.ClientId());
        }

        Client author = clientService.getClient(reqBody.ClientId());
        Event event = eventService.getEvent(reqBody.eventId());

        Comment comment = reqBody.toEntity(event, author);
        comment = commentRepository.save(comment);
        return CommonCommentDetail.from(comment);
    }
}
