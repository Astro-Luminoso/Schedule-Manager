package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostNewComment;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonCommentDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Comment;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.exception.EventNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.CommentRepository;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import dev.nbcsparta.assignment.schedulemanager.service.CommentService;
import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void test_Add_New_Comment_Success() {
        long clientId = 1L;
        long eventId = 1L;
        PostNewComment reqBody = new PostNewComment("This is an awesome comments!!", eventId, clientId);
        Client author = new Client("Test User", "user@test.com", "qwer1234");
        ReflectionTestUtils.setField(author, "id", clientId);
        Event event = new Event("Test Event", "This is a test event.", author);
        ReflectionTestUtils.setField(event, "id", eventId);

        when(clientService.getClient(clientId)).thenReturn(author);
        when(eventService.getEvent(eventId)).thenReturn(event);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CommonCommentDetail resBody = commentService.createComment(reqBody, clientId);

        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(reqBody.content(), resBody.content());
        Assertions.assertEquals(reqBody.eventId(), resBody.eventId());
        Assertions.assertEquals(reqBody.ClientId(), resBody.authorId());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    public void test_Add_New_Comment_Forbidden_When_Session_User_Does_Not_Match_Author() {
        long sessionId = 1L;
        long targetAuthorId = 2L;
        long eventId = 99L;
        PostNewComment reqBody = new PostNewComment("content", eventId, targetAuthorId);

        ClientNotAuthorisedException ex = Assertions.assertThrows(
                ClientNotAuthorisedException.class,
                () -> commentService.createComment(reqBody, sessionId)
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(clientService, never()).getClient(any(Long.class));
        verify(eventService, never()).getEvent(any(Long.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void test_Add_New_Comment_Not_Found_When_Author_Does_Not_Exist() {
        long clientId = 1L;
        long eventId = 10L;
        PostNewComment reqBody = new PostNewComment("content", eventId, clientId);

        when(clientService.getClient(clientId)).thenThrow(new AuthorNotFoundException(HttpStatus.NOT_FOUND));

        AuthorNotFoundException ex = Assertions.assertThrows(
                AuthorNotFoundException.class,
                () -> commentService.createComment(reqBody, clientId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(eventService, never()).getEvent(any(Long.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void test_Add_New_Comment_Not_Found_When_Event_Does_Not_Exist() {
        long clientId = 1L;
        long eventId = 10L;
        PostNewComment reqBody = new PostNewComment("content", eventId, clientId);
        Client author = new Client("Test User", "user@test.com", "qwer1234");
        ReflectionTestUtils.setField(author, "id", clientId);

        when(clientService.getClient(clientId)).thenReturn(author);
        when(eventService.getEvent(eventId)).thenThrow(new EventNotFoundException(HttpStatus.NOT_FOUND, eventId));

        EventNotFoundException ex = Assertions.assertThrows(
                EventNotFoundException.class,
                () -> commentService.createComment(reqBody, clientId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
