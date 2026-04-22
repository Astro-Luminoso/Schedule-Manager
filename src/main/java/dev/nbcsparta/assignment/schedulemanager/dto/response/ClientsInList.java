package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;

import java.util.List;

public record ClientsInList(List<CommonClientDetail> clientsList, int total) {

    public static ClientsInList from(List<Client> data) {
        List<CommonClientDetail> clientsList = data.stream()
                .map(CommonClientDetail::new)
                .toList();
        return new ClientsInList(clientsList, clientsList.size());
    }
}
