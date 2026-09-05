package com.db.alerttriage.triage.service;

import com.db.alerttriage.triage.entity.TriageDecisionEntity;
import com.db.alerttriage.triage.repository.TriageDecisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TriageReviewService {

    private final TriageDecisionRepository triageDecisionRepository;

    public TriageReviewService(
            TriageDecisionRepository triageDecisionRepository
    ) {
        this.triageDecisionRepository = triageDecisionRepository;
    }

    @Transactional
    public void approve(
            Long alertId,
            String reviewedBy,
            String comment
    ) {
        TriageDecisionEntity decision = getDecision(alertId);

        decision.approve(
                reviewedBy,
                comment,
                Instant.now()
        );
    }

    @Transactional
    public void reject(
            Long alertId,
            String reviewedBy,
            String comment
    ) {
        TriageDecisionEntity decision = getDecision(alertId);

        decision.reject(
                reviewedBy,
                comment,
                Instant.now()
        );
    }

    private TriageDecisionEntity getDecision(Long alertId) {
        return triageDecisionRepository.findByAlertId(alertId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Triage decision not found for alert: " + alertId
                        )
                );
    }
}