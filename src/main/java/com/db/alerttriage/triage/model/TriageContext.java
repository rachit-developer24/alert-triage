package com.db.alerttriage.triage.model;

import com.db.alerttriage.inventory.model.AssetFacts;
import com.db.alerttriage.vulnerability.model.VulnerabilityAdvisory;

import java.math.BigDecimal;

public record TriageContext(
        String cveId,
        String hostname,
        BigDecimal cvssScore,
        String description,
        AssetFacts assetFacts,
        VulnerabilityAdvisory vulnerabilityAdvisory
) {
}