package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> attemptLogin(
            @Valid @RequestBody PostLoginRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionUser sessionUser = loginService.executeLogin(request);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("LOGIN_USER", sessionUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> attemptLogout(
            HttpServletRequest request,
            HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
