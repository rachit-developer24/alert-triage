package com.db.alerttriage.triage.model;

public record AdvisoryReadRequest(
        String cveId,
        String advisoryText
) {
}