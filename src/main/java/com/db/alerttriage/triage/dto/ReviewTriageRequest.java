package com.db.alerttriage.triage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewTriageRequest(

        @NotBlank
        @Size(max = 255)
        String reviewedBy,

        @Size(max = 1000)
        String comment

) {
}
