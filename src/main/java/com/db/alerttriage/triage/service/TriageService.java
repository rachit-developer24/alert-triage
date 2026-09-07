package com.db.alerttriage.triage.service;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.inventory.model.AssetFacts;
import com.db.alerttriage.triage.ai.TriageModel;
import com.db.alerttriage.triage.entity.TriageDecisionEntity;
import com.db.alerttriage.triage.model.*;
import com.db.alerttriage.triage.repository.TriageDecisionRepository;
import com.db.alerttriage.triage.validation.FactsValidator;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TriageService {

    private final TriageContextFactory triageContextFactory;
    private final TriageModel triageModel;
    private final FactsValidator factsValidator;
    private final TriageDecisionService triageDecisionService;
    private final TriageDecisionRepository triageDecisionRepository;


    public TriageService(

            TriageContextFactory triageContextFactory,

            TriageModel triageModel ,

            FactsValidator factsValidator,

            TriageDecisionService triageDecisionService,

            TriageDecisionRepository triageDecisionRepository

    ) {

        this.triageContextFactory = triageContextFactory;

        this.triageModel = triageModel;

        this.factsValidator = factsValidator;

        this.triageDecisionService = triageDecisionService;

        this.triageDecisionRepository = triageDecisionRepository;

    }


    public TriageDecision triage(Alert alert) {

        TriageContext context =
                triageContextFactory.create(alert);


        AdvisoryReadRequest modelRequest =
                new AdvisoryReadRequest(
                        context.cveId(),
                        context.vulnerabilityAdvisory().advisoryText()
                );

        AdvisoryFacts facts;
        TriageDecision decision;

        try {
            facts = triageModel.interpret(modelRequest);

            if (facts == null) {
                decision = new TriageDecision(
                        TriageAction.REVIEW,
                        "AI model returned no result - manual review required"
                );
            } else {

                boolean valid =
                        factsValidator.isValid(
                                facts,
                                context.vulnerabilityAdvisory().advisoryText()
                        );

                if (!valid) {
                    decision = new TriageDecision(
                            TriageAction.REVIEW,
                            "AI evidence validation failed"
                    );
                } else {
                    decision = triageDecisionService.decide(
                            alert,
                            facts
                    );
                }
            }

        } catch (RuntimeException ex) {

            facts = null;

            decision = new TriageDecision(
                    TriageAction.REVIEW,
                    "AI model unavailable - manual review required"
            );
        }


        AssetFacts asset = context.assetFacts();

        TriageDecisionEntity entity =
                new TriageDecisionEntity(
                        alert,
                        decision.action(),
                        decision.reason(),

                        asset.environment(),
                        asset.internetExposed(),
                        asset.businessCriticality(),
                        asset.installedVersion(),
                        asset.ownerTeam(),
                        asset.vendor(),
                        asset.model(),

                        facts == null ? null : facts.affectedVersionRange(),
                        facts == null ? null : facts.fixedVersion(),
                        facts == null ? null : facts.remediationSummary(),

                        Instant.now()
                );

        triageDecisionRepository.save(entity);

        return decision;
    }


}
