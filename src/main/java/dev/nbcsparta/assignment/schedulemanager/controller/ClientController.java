package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonClientDetail;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
