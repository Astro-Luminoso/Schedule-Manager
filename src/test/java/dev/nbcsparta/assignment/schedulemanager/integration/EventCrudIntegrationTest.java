package dev.nbcsparta.assignment.schedulemanager.integration;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PatchEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.dto.response.EventListResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import dev.nbcsparta.assignment.schedulemanager.repository.EventRepository;
import tools.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventCrudIntegrationTest {

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
    public void getEvents_And_Return_All_Events() throws Exception {
        eventRepository.save(new Event("Team Meeting", "Discuss sprint", loginUser));
        eventRepository.save(new Event("Code Review", "Review PRs", loginUser));
        eventRepository.save(new Event("Client Sync", "Weekly sync", anotherUser));

        MvcResult result = mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andReturn();

        EventListResponse resBody = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                EventListResponse.class
        );

        Assertions.assertEquals(3, resBody.totalEvents());

        Assertions.assertTrue(
                resBody.eventsList().stream().allMatch(event -> event.date() != null && !event.date().isBlank())
        );
    }

    @Test
    public void getEvents_And_Filter_By_AuthorId() throws Exception {
        eventRepository.save(new Event("A Event 1", "By Jane", loginUser));
        eventRepository.save(new Event("A Event 2", "Also by Jane", loginUser));
        eventRepository.save(new Event("B Event", "By John", anotherUser));

        MvcResult result = mockMvc.perform(get("/events").param("authorId", String.valueOf(loginUser.getId())))
                .andExpect(status().isOk())
                .andReturn();

        EventListResponse resBody = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                EventListResponse.class
        );

        Assertions.assertEquals(2, resBody.totalEvents());
        Assertions.assertTrue(
                resBody.eventsList().stream().allMatch(event -> event.authorName().equals(loginUser.getUserName()))
        );
    }

    @Test
    public void getEvents_And_Return_Empty_When_Author_Has_No_Events() throws Exception {
        eventRepository.save(new Event("Existing Event", "By Jane", loginUser));

        MvcResult result = mockMvc.perform(get("/events").param("authorId", "999999"))
                .andExpect(status().isOk())
                .andReturn();

        EventListResponse resBody = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                EventListResponse.class
        );

        Assertions.assertEquals(0, resBody.totalEvents());
        Assertions.assertTrue(resBody.eventsList().isEmpty());
    }

    @Test
    public void createEvent_And_Success() throws Exception {
        MockHttpSession session = loginAndGetSession(loginUser.getEmail(), "q1w2e3r4");
        PostEventRequest reqBody = new PostEventRequest("Write Tests", "Add integration coverage");

        MvcResult result = mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isCreated())
                .andReturn();

        CommonEventResponse resBody = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CommonEventResponse.class
        );

        Assertions.assertEquals(reqBody.title(), resBody.title());
        Assertions.assertEquals(reqBody.description(), resBody.description());
        Assertions.assertEquals(loginUser.getUserName(), resBody.authorName());
        Assertions.assertTrue(eventRepository.findById(resBody.id()).isPresent());
    }

    @Test
    public void createEvent_And_Unauthorized_Without_Session() throws Exception {
        PostEventRequest reqBody = new PostEventRequest("Write Tests", "Add integration coverage");

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateEvent_And_Success() throws Exception {
        Event existingEvent = eventRepository.save(new Event("Old Title", "Old Description", loginUser));
        MockHttpSession session = loginAndGetSession(loginUser.getEmail(), "q1w2e3r4");
        PatchEventRequest reqBody = new PatchEventRequest("Updated Title", "Updated Description");

        mockMvc.perform(patch("/events/{id}", existingEvent.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isOk());

        Event updatedEvent = eventRepository.findById(existingEvent.getId()).orElseThrow();
        Assertions.assertEquals(reqBody.title(), updatedEvent.getTitle());
        Assertions.assertEquals(reqBody.description(), updatedEvent.getDescription());
    }

    @Test
    public void deleteEvent_And_Success() throws Exception {
        Event existingEvent = eventRepository.save(new Event("To Delete", "Delete me", loginUser));
        MockHttpSession session = loginAndGetSession(loginUser.getEmail(), "q1w2e3r4");

        mockMvc.perform(delete("/events/{id}", existingEvent.getId())
                        .session(session))
                .andExpect(status().isNoContent());

        Assertions.assertTrue(eventRepository.findById(existingEvent.getId()).isEmpty());
    }

    private MockHttpSession loginAndGetSession(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostLoginRequest(email, password))))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        Assertions.assertNotNull(session);
        return session;
    }
}
