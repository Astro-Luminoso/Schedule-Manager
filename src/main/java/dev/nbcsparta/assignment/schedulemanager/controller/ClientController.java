package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.UpdateClientDetail;
import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonClientDetail;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<ClientsInList> retrieveAllClients() {
        ClientsInList resBody = clientService.retrieveAllClients();
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonClientDetail> retrieveClientById(@PathVariable Long id) {
        CommonClientDetail resBody = clientService.retrieveClientById(id);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonClientDetail> updateClientById(
            @PathVariable Long id,
            @RequestBody UpdateClientDetail reqBody,
            @SessionAttribute(name = "LOGIN_USER") SessionUser sessionUser
            ){
        if (sessionUser == null) {
            throw new ClientNotAuthorisedException(HttpStatus.UNAUTHORIZED);
        }
        CommonClientDetail resBody = clientService.putClientById(id, reqBody, sessionUser.id());
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }
}
