package com.db.alerttriage.triage.controller;

import com.db.alerttriage.triage.dto.ReviewTriageRequest;
import com.db.alerttriage.triage.model.TriageDecisionResponse;
import com.db.alerttriage.triage.service.TriageReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/alerts/{alertId}/triage")
public class TriageReviewController {

    private final TriageReviewService triageReviewService;

    public TriageReviewController(
            TriageReviewService triageReviewService
    ) {
        this.triageReviewService = triageReviewService;
    }

    @PostMapping("/approve")
    public ResponseEntity<Void> approve(
            @PathVariable Long alertId,
            @Valid @RequestBody ReviewTriageRequest request
    ) {

        triageReviewService.approve(
                alertId,
                request.reviewedBy(),
                request.comment()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long alertId,
            @Valid @RequestBody ReviewTriageRequest request
    ) {

        triageReviewService.reject(
                alertId,
                request.reviewedBy(),
                request.comment()
        );

        return ResponseEntity.noContent().build();
    }
}