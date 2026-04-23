package dev.nbcsparta.assignment.schedulemanager.integration;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.UpdateClientDetail;
import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonClientDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder encoder;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        for (int i = 1; i <= 3; i++) {
            clientRepository.save(new Client(
                    "Test User " + i,
                    "user" + i + "@dummy.dev",
                    "qwer1234"
            ));
        }
    }

    @Test
    public void test_retrieve_all_Client() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        ClientsInList resBody = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ClientsInList.class
        );

        Assertions.assertNotNull(resBody);
        Assertions.assertEquals(3, resBody.total());
        Assertions.assertNotNull(resBody.clientsList());
        Assertions.assertFalse(resBody.clientsList().isEmpty());
    }

    @Test
    public void test_retrieve_Client_by_id() throws Exception {
        Client client = clientRepository.save(new Client("Test User 4", "user4@dummy.dev", "q1w2e3r4"));
        MvcResult mvcResult = mockMvc.perform(get("/users/{id}", client.getId()))
                .andExpect(status().isOk())
                .andReturn();

        CommonClientDetail resBody = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CommonClientDetail.class
        );

        Assertions.assertEquals("Test User 4", resBody.userName());
        Assertions.assertEquals("user4@dummy.dev", resBody.email());
        Assertions.assertNotNull(resBody.date());
        Assertions.assertFalse(resBody.date().isBlank());
    }

    @Test
    public void test_Retrieve_Client_by_Invalid_Id_Return_NOT_FOUND() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_update_Client_by_Id_and_Success() throws Exception {
        Client target = clientRepository.save(new Client("Old Name", "old@dummy.dev", encoder.encode("qwer1234")));
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "qwer1234");
        MockHttpSession session = createSession(target.getId(), target.getEmail());

        MvcResult mvcResult = mockMvc.perform(put("/users/{id}", target.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isOk())
                .andReturn();

        CommonClientDetail resBody = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CommonClientDetail.class
        );

        Assertions.assertEquals("New Name", resBody.userName());
        Assertions.assertEquals("new@dummy.dev", resBody.email());
    }

    @Test
    public void test_update_Client_by_Id_without_session_returns_BAD_REQUEST() throws Exception {
        Client target = clientRepository.save(new Client("Old Name", "old@dummy.dev", "qwer1234"));
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "qwer1234");

        mockMvc.perform(put("/users/{id}", target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_update_Client_by_Id_With_Wrong_Password_returns_BAD_REQUEST() throws Exception {
        Client target = clientRepository.save(new Client("Old Name", "old@dummy.dev", encoder.encode("q1w2e3r4")));
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "qwer1234");
        MockHttpSession session = createSession(target.getId(), target.getEmail());

        mockMvc.perform(put("/users/{id}", target.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_update_Client_by_Id_with_different_sessionUser_returns_FORBIDDEN() throws Exception {
        Client target = clientRepository.save(new Client("Old Name", "old@dummy.dev", "qwer1234"));
        Client another = clientRepository.save(new Client("Another", "another@dummy.dev", "qwer1234"));
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "qwer1234");
        MockHttpSession session = createSession(another.getId(), another.getEmail());

        mockMvc.perform(put("/users/{id}", target.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_delete_Client_by_Id_and_Success() throws Exception {
        Client target = clientRepository.save(new Client("To Delete", "delete@dummy.dev", "qwer1234"));
        MockHttpSession session = createSession(target.getId(), target.getEmail());

        mockMvc.perform(delete("/users/{id}", target.getId())
                        .session(session))
                .andExpect(status().isOk());

        Assertions.assertEquals("Deleted_User_" + target.getId(), target.getUserName());
        Assertions.assertNull(target.getEmail());
    }

    @Test
    public void test_delete_Client_by_Id_without_session_returns_BAD_REQUEST() throws Exception {
        Client target = clientRepository.save(new Client("To Delete", "delete.no.session@dummy.dev", "qwer1234"));

        mockMvc.perform(delete("/users/{id}", target.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_delete_Client_by_Id_with_different_sessionUser_returns_FORBIDDEN() throws Exception {
        Client target = clientRepository.save(new Client("Target", "target@dummy.dev", "qwer1234"));
        Client another = clientRepository.save(new Client("Another", "another.delete@dummy.dev", "qwer1234"));
        MockHttpSession session = createSession(another.getId(), another.getEmail());

        mockMvc.perform(delete("/users/{id}", target.getId())
                        .session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    public void test_delete_Client_by_Invalid_Id_returns_NOT_FOUND() throws Exception {
        Client loginClient = clientRepository.save(new Client("Login User", "login.delete@dummy.dev", encoder.encode("qwer1234")));
        MockHttpSession session = createSession(loginClient.getId(), loginClient.getEmail());

        mockMvc.perform(delete("/users/{id}", 999999L)
                        .session(session))
                .andExpect(status().isNotFound());
    }

    private MockHttpSession createSession(Long id, String email) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", new SessionUser(id, email));
        return session;
    }
}
