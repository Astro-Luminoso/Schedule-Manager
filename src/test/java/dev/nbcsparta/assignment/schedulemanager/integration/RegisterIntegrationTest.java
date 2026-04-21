package dev.nbcsparta.assignment.schedulemanager.integration;


import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.SimpleClientResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegisterIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ClientRepository clientRepository;

    @Test
    public void register_New_Client_And_Success() throws Exception {
        PostRegisterRequest reqBody = new PostRegisterRequest("Test User", "dummy@dummy.dev", "qwer1234");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(result -> {
                    String res = result.getResponse().getContentAsString();
                    SimpleClientResponse resBody = new ObjectMapper().readValue(res, SimpleClientResponse.class);
                    Assertions.assertEquals(reqBody.userName(), resBody.userName());
                    Assertions.assertEquals(reqBody.email(), resBody.email());
                })
                .andExpect(status().isCreated());

        Client savedClient = clientRepository.findByEmail(reqBody.email()).orElse(null);
        Assertions.assertNotNull(savedClient);
        Assertions.assertEquals(reqBody.userName(), savedClient.getUserName());
        Assertions.assertEquals(reqBody.email(), savedClient.getEmail());
    }


    @Test
    public void register_New_Client_And_Fail_Because_Of_Invalid_Values() throws Exception {
        PostRegisterRequest reqBody = new PostRegisterRequest("Ter", "dummy@dummy.dev", "qwer1234");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
        reqBody = new PostRegisterRequest("test user", "dummyummy.dev", "qwer1234");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
        reqBody = new PostRegisterRequest("Test User", "dummy@dummy.dev", "qwer134");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());

        List<Client> clients = clientRepository.findAll();
        Assertions.assertEquals(0, clients.size());
    }
}

