package com.db.alerttriage.triage.model;

import java.util.List;

public record AdvisoryFacts(
        String affectedVersionRange,
        String fixedVersion,
        String remediationSummary,
        List<String> exploitPrerequisites,
        List<String> mitigatingControls,
        List<EvidenceQuote> evidence
) {
}