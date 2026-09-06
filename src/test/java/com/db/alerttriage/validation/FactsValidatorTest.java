package com.db.alerttriage.validation;
import static org.assertj.core.api.Assertions.assertThat;

import com.db.alerttriage.triage.model.AdvisoryFacts;
import com.db.alerttriage.triage.model.EvidenceQuote;
import com.db.alerttriage.triage.validation.FactsValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FactsValidatorTest {

    private final FactsValidator factsValidator = new FactsValidator();

    @Test
    void rejectsFactsWhenEvidenceIsEmpty(){

        AdvisoryFacts facts = new AdvisoryFacts(
                "before 9.16.6",
                "9.16.6",
                "Upgrade to 9.16.6 or later",
                List.of(),
                List.of(),
                List.of()
        );

        boolean valid = factsValidator.isValid(
                facts,
                "Versions before 9.16.6 are affected."
        );
        assertThat(valid).isFalse();

    }

    @Test
    void rejectsFactsWhenEvidenceQuoteIsNotInAdvisory() {

        AdvisoryFacts facts = new AdvisoryFacts(
                "before 9.16.6",
                "9.16.6",
                "Upgrade to 9.16.6 or later",
                List.of(),
                List.of(),
                List.of(
                        new EvidenceQuote(
                                "fixedVersion",
                                "Upgrade immediately to 9.16.7."
                        )
                )
        );

        boolean valid = factsValidator.isValid(
                facts,
                "Versions before 9.16.6 are affected. Customers should upgrade to 9.16.6 or later."
        );

        assertThat(valid).isFalse();
    }

}
