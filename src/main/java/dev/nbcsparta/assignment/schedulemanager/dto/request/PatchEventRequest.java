package dev.nbcsparta.assignment.schedulemanager.dto.request;

import jakarta.validation.constraints.Pattern;

public record PatchEventRequest(
        @Pattern(
                regexp = "^$|^.{4,}$",
                message = "title must be empty or at least 4 characters"
        )
        String title,


        String description
) {
}
