package com.db.alerttriage.triage.validation;

import com.db.alerttriage.triage.model.AdvisoryFacts;
import com.db.alerttriage.triage.model.EvidenceQuote;
import org.springframework.stereotype.Component;


@Component
public class FactsValidator {

    public boolean isValid(
            AdvisoryFacts facts,
            String advisoryText
    ) {

        if (facts.evidence() == null || facts.evidence().isEmpty()) {
            return false;
        }

        for (EvidenceQuote evidence : facts.evidence()) {

            if (!advisoryText.contains(evidence.quote())) {
                return false;
            }
        }

        return true;
    }
}