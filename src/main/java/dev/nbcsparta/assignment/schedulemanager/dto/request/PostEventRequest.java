package dev.nbcsparta.assignment.schedulemanager.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostEventRequest(

        @NotBlank
        @Size(min = 4)
        String title,

        @NotBlank
        String description) {
}
