package com.db.alerttriage.triage.ai;

import com.db.alerttriage.triage.model.AdvisoryFacts;
import com.db.alerttriage.triage.model.AdvisoryReadRequest;
import com.db.alerttriage.triage.model.EvidenceQuote;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("fake")
public class FakeTriageModel implements TriageModel {

    @Override
    public AdvisoryFacts interpret(AdvisoryReadRequest request) {
        return new AdvisoryFacts(
                "before 9.16.6",
                "9.16.6",
                "Upgrade to version 9.16.6 or later",
                List.of("Network access to the management interface"),
                List.of("Restrict management access to trusted networks"),
                List.of(
                        new EvidenceQuote(
                                "fixedVersion",
                                "Customers should upgrade to 9.16.6 or later."
                        )
                )
        );
    }
}