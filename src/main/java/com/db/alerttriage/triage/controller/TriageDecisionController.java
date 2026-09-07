package com.db.alerttriage.triage.controller;

import com.db.alerttriage.triage.model.TriageDecisionResponse;
import com.db.alerttriage.triage.service.TriageReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/triage-decisions")
public class TriageDecisionController {

    private final TriageReviewService triageReviewService;

    public TriageDecisionController(
            TriageReviewService triageReviewService
    ) {
        this.triageReviewService = triageReviewService;
    }

    @GetMapping("/pending")
    public List<TriageDecisionResponse> getPendingDecisions() {
        return triageReviewService.getPendingDecisions();
    }
}