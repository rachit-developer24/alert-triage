package com.db.alerttriage.alert.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateAlertRequest(

        @NotBlank
        @Size(max = 100)
        String sourceAlertId,

        @NotBlank
        @Size(max = 50)
        String cveId,

        @NotBlank
        @Size(max = 255)
        String hostname,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("10.0")
        BigDecimal cvssScore,

        @NotBlank
        String description,

        @NotNull
        Instant detectedAt


) {
}
