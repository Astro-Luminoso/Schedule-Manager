package dev.nbcsparta.assignment.schedulemanager.dto.response;

import java.util.List;

public record EventListResponse(List<CommonEventResponse> eventsList, int totalEvents) {

    public static EventListResponse from (List<CommonEventResponse> eventsList) {
        return new EventListResponse(eventsList, eventsList.size());
    }
}
