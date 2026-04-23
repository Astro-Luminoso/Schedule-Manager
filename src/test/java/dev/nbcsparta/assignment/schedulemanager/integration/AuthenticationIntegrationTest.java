package dev.nbcsparta.assignment.schedulemanager.integration;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import jakarta.servlet.http.HttpSession;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    public void beforeEach() {
        clientRepository.save(
                new Client("Jane Doe", "jane.doe@sparta.com", encoder.encode("q1w2e3r4")));
    }

    @Test
    public void login_And_Success() throws Exception {
        PostLoginRequest reqBody = new PostLoginRequest("jane.doe@sparta.com", "q1w2e3r4");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    HttpSession session = result.getRequest().getSession(false);
                    Assertions.assertNotNull(session);
                    SessionUser sessionUser = new ObjectMapper().convertValue(session.getAttribute("LOGIN_USER"), SessionUser.class);
                    Assertions.assertEquals(reqBody.email(), sessionUser.email());
                });
    }

    @Test
    public void login_And_Unauthorized() throws Exception {
        PostLoginRequest reqBody = new PostLoginRequest("jane.doe@sparta.com", "qwer1234");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_And_BadRequest() throws Exception {
        PostLoginRequest reqBody = new PostLoginRequest("jane.doearta.com", "q1w2e3r4");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());

        reqBody = new PostLoginRequest("jane.doe@sparta.com", "qwer34");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logout_And_Success() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new PostLoginRequest("jane.doe@sparta.com", "q1w2e3r4"))))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        assert session != null;
        mockMvc.perform(post("/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    HttpSession after = result.getRequest().getSession(false);
                    Assertions.assertNull(after);
                });
        // with no session should status 200
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk());
    }
}
