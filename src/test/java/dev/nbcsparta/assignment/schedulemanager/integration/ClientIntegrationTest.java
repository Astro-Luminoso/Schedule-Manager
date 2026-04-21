package dev.nbcsparta.assignment.schedulemanager.integration;

import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonClientDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

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
}
