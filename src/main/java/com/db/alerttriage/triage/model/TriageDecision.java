package com.db.alerttriage.triage.model;

public record TriageDecision(
        TriageAction action,
        String reason
) {
}