package com.db.alerttriage.triage.service;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.triage.entity.TriageDecisionEntity;
import com.db.alerttriage.triage.model.ReviewStatus;
import com.db.alerttriage.triage.model.TriageDecisionResponse;
import com.db.alerttriage.triage.repository.TriageDecisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<TriageDecisionResponse> getPendingDecisions() {

        return triageDecisionRepository
                .findByReviewStatusOrderByDecidedAtDesc(
                        ReviewStatus.PENDING
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }
    private TriageDecisionResponse toResponse(
            TriageDecisionEntity entity
    ) {

        Alert alert = entity.getAlert();

        return new TriageDecisionResponse(
                alert.getId(),
                alert.getCveId(),
                alert.getHostname(),
                alert.getCvssScore(),
                alert.getSeverity(),

                entity.getAction(),
                entity.getReason(),
                entity.getReviewStatus(),
                entity.getDecidedAt()
        );
    }

}