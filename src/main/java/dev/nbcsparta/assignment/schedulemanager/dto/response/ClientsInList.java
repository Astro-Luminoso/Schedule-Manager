package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;

import java.util.List;

public record ClientsInList(List<Client> clientsList, int total) {

    public ClientsInList(List<Client> data) {
        this(data, data.size());
    }
}
