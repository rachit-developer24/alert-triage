package com.db.alerttriage.alert.dto;

import com.db.alerttriage.alert.entity.AlertStatus;
import com.db.alerttriage.alert.entity.Severity;

import java.math.BigDecimal;
import java.time.Instant;

public record AlertResponse(
        Long id,
        String sourceAlertId,
        String cveId,
        String hostname,
        BigDecimal cvssScore,
        Severity severity,
        String description,
        Instant detectedAt,
        Instant receivedAt,
        AlertStatus status
) {
}
