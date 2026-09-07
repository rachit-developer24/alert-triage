package com.db.alerttriage.triage.model;

import com.db.alerttriage.alert.entity.Severity;
import com.db.alerttriage.triage.model.ReviewStatus;
import com.db.alerttriage.triage.model.TriageAction;

import java.math.BigDecimal;
import java.time.Instant;

public record TriageDecisionResponse(
        Long alertId,
        String cveId,
        String hostname,
        BigDecimal cvssScore,
        Severity severity,
        TriageAction action,
        String reason,
        ReviewStatus reviewStatus,
        Instant decidedAt
) {
}