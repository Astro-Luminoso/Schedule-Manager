package dev.nbcsparta.assignment.schedulemanager.unit.controller;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.controller.RegisterController;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.SimpleClientResponse;
import dev.nbcsparta.assignment.schedulemanager.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = RegisterController.class)
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    PasswordEncoder encoder;

    @MockitoBean
    RegisterService registerService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    public void testRegisterAndSuccess() throws Exception {
        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        when(registerService.executeRegister(reqBody))
                .thenReturn(new SimpleClientResponse(reqBody.userName(), reqBody.email()));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value(reqBody.userName()))
                .andExpect(jsonPath("$.email").value(reqBody.email()));
    }

    @Test
    public void testRegisterAndInvalidPassword() throws Exception{
        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer234");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());

        reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "  ");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());

        reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterAndInvalidEmail() throws Exception {
        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.temmy.dev", "qwer1234");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterAndInvalidUserName() throws Exception {
        PostRegisterRequest reqBody = new PostRegisterRequest("ter", "test.tester@dummy.dev", "qwer1234");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest());
    }


}
