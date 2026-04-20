package dev.nbcsparta.assignment.schedulemanager.unit.controller;

import dev.nbcsparta.assignment.schedulemanager.controller.EventController;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    public void createNewEventAndSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", new SessionUser(1L, "jane.doe@dummy.org"));
        when(eventService.createEvent(new PostEventRequest("Test Event", "This is a test event."), 1L))
                .thenReturn(new CommonEventResponse(1L, "Test Event", "This is a test event.", "260420", "Jane Doe"));

        mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("Test Event", "This is a test event."))))
                .andExpect(status().isCreated());
    }

    @Test
    public void createNewEventAndInvalidSession() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("Test Event", "This is a test event."))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/events")
                        .session(new MockHttpSession()) /* with no LOGIN_USER attribute */
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("Test Event", "This is a test event."))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createNewEventAndInvalidBody() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", new SessionUser(1L, "jane.doe@dummy.org"));

        mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("Tes", "This is a test event."))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest(" ", "This is a test event."))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("", "This is a test event."))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("Test Event", ""))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/events")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostEventRequest("Test Event", "  "))))
                .andExpect(status().isBadRequest());
    }
}
