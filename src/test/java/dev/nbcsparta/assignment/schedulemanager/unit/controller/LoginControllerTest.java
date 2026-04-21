package dev.nbcsparta.assignment.schedulemanager.unit.controller;

import dev.nbcsparta.assignment.schedulemanager.controller.LoginController;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.service.LoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    public void executeLoginAndSuccess() throws Exception {

        SessionUser dummyUser = new SessionUser(1L, "jane.doe@dummy.dev");
        given(loginService.executeLogin(any(PostLoginRequest.class)))
                .willReturn(dummyUser);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest("jane.doe@dummy.dev", "qwer1234"))))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("LOGIN_USER", dummyUser));
    }

    @Test
    public void executeLoginAndPasswordInvalid() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest("jane.doe@dummy.dev", "qwer234"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest("jane.doe@dummy.dev", ""))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest("jane.doe@dummy.dev", "  "))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void executeLoginAndEmailInvalid() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest("jane.doemy.dev", "qwer1234"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest(" ", "qwer234"))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper()
                                .writeValueAsString(
                                        new PostLoginRequest("", "qwer234"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testExecuteLogoutAndSuccess() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_USER", new SessionUser(1L, "jane.doe@dummy.org"));
        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttributeDoesNotExist("LOGIN_USER"));

        Assertions.assertTrue(session.isInvalid());
    }
    @Test
    public void testExecuteLogoutWithNoSessionAndSuccess() throws Exception {

        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk());
    }
}
