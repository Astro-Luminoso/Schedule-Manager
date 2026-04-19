package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.SimpleClientResponse;
import dev.nbcsparta.assignment.schedulemanager.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleClientResponse> register(
            @Valid @RequestBody PostRegisterRequest reqBody
    ) {
        SimpleClientResponse resBody = registerService.executeRegister(reqBody);
        return ResponseEntity.status(HttpStatus.CREATED).body(resBody);
    }
}
