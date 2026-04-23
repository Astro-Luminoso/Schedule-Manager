package dev.nbcsparta.assignment.schedulemanager.integration;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.request.NewComment;
import dev.nbcsparta.assignment.schedulemanager.dto.response.AllCommentsByEvent;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommentDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Comment;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import dev.nbcsparta.assignment.schedulemanager.repository.CommentRepository;
import dev.nbcsparta.assignment.schedulemanager.repository.EventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Client loginUser;
    private Client anotherUser;

    @BeforeEach
    public void beforeEach() {
        loginUser = clientRepository.save(new Client(
                "Jane Doe",
                "jane.doe@sparta.com",
                encoder.encode("q1w2e3r4")
        ));
        anotherUser = clientRepository.save(new Client(
                "John Doe",
                "john.doe@sparta.com",
                encoder.encode("a1s2d3f4")
        ));
    }

    @Test
    public void createComment_And_Success() throws Exception {
        Event event = eventRepository.save(new Event("Team Meeting", "Discuss sprint", loginUser));
        MockHttpSession session = loginAndGetSession(loginUser.getEmail(), "q1w2e3r4");
        NewComment reqBody = new NewComment("Looks good!", event.getId());

        MvcResult result = mockMvc.perform(post("/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentDetail resBody = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CommentDetail.class
        );

        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(reqBody.content(), resBody.content());
        Assertions.assertEquals(loginUser.getId(), resBody.client().id());
        Assertions.assertEquals(loginUser.getUserName(), resBody.client().userName());
        Assertions.assertEquals(loginUser.getEmail(), resBody.client().email());
        Assertions.assertTrue(resBody.id() > 0);

        List<Comment> savedComments = commentRepository.findByEventId(event.getId());
        Assertions.assertEquals(1, savedComments.size());
        Assertions.assertEquals(reqBody.content(), savedComments.getFirst().getContent());
    }

    @Test
    public void createComment_And_Unauthorized_Without_Session() throws Exception {
        Event event = eventRepository.save(new Event("Team Meeting", "Discuss sprint", loginUser));
        NewComment reqBody = new NewComment("Looks good!", event.getId());

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isUnauthorized());

        Assertions.assertTrue(commentRepository.findByEventId(event.getId()).isEmpty());
    }

    @Test
    public void createComment_And_NotFound_When_Event_Does_Not_Exist() throws Exception {
        MockHttpSession session = loginAndGetSession(loginUser.getEmail(), "q1w2e3r4");
        NewComment reqBody = new NewComment("Looks good!", 999999L);

        mockMvc.perform(post("/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isNotFound());

        Assertions.assertTrue(commentRepository.findAll().isEmpty());
    }

    @Test
    public void createComment_And_Forbidden_When_Session_User_Is_Not_Event_Author() throws Exception {
        Event event = eventRepository.save(new Event("Team Meeting", "Discuss sprint", loginUser));
        MockHttpSession session = loginAndGetSession(anotherUser.getEmail(), "a1s2d3f4");
        NewComment reqBody = new NewComment("Looks good!", event.getId());

        mockMvc.perform(post("/comments")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());

        Assertions.assertTrue(commentRepository.findByEventId(event.getId()).isEmpty());
    }

    @Test
    public void getComments_And_Return_Only_Target_Event_Comments() throws Exception {
        Event loginUserEvent = eventRepository.save(new Event("Team Meeting", "Discuss sprint", loginUser));
        Event anotherUserEvent = eventRepository.save(new Event("Design Review", "Review mockups", anotherUser));

        MockHttpSession loginSession = loginAndGetSession(loginUser.getEmail(), "q1w2e3r4");
        MockHttpSession anotherSession = loginAndGetSession(anotherUser.getEmail(), "a1s2d3f4");

        mockMvc.perform(post("/comments")
                        .session(loginSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewComment("First comment", loginUserEvent.getId()))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/comments")
                        .session(anotherSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NewComment("Second comment", anotherUserEvent.getId()))))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/comments").param("eventId", String.valueOf(loginUserEvent.getId())))
                .andExpect(status().isOk())
                .andReturn();

        AllCommentsByEvent resBody = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AllCommentsByEvent.class
        );

        Assertions.assertEquals(1, resBody.total());
        Assertions.assertEquals(1, resBody.comments().size());
        Assertions.assertEquals("First comment", resBody.comments().getFirst().content());
        Assertions.assertEquals(loginUser.getId(), resBody.comments().getFirst().client().id());
    }

    @Test
    public void getComments_And_NotFound_When_Event_Does_Not_Exist() throws Exception {
        mockMvc.perform(get("/comments").param("eventId", "999999"))
                .andExpect(status().isNotFound());
    }

    private MockHttpSession loginAndGetSession(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostLoginRequest(email, password))))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        Assertions.assertNotNull(session);
        SessionUser sessionUser = (SessionUser) session.getAttribute("LOGIN_USER");
        Assertions.assertNotNull(sessionUser);
        Assertions.assertEquals(email, sessionUser.email());
        return session;
    }
}

