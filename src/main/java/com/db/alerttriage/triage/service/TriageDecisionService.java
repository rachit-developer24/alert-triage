package com.db.alerttriage.triage.service;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.triage.model.AdvisoryFacts;
import com.db.alerttriage.triage.model.TriageAction;
import com.db.alerttriage.triage.model.TriageDecision;
import com.db.alerttriage.triage.repository.TriageDecisionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class TriageDecisionService {

    public TriageDecision decide(
            Alert alert,
            AdvisoryFacts facts

    ) {

        if (alert.getCvssScore().compareTo(new BigDecimal("9.0")) >= 0) {
            return new TriageDecision(
                    TriageAction.ESCALATE,
                    "Critical CVSS score"
            );
        }

        return new TriageDecision(
                TriageAction.REVIEW,
                "Manual review required"
        );
    }
}