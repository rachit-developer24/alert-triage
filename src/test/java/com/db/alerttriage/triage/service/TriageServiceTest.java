package com.db.alerttriage.triage.service;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.inventory.entity.BusinessCriticality;
import com.db.alerttriage.inventory.entity.Environment;
import com.db.alerttriage.inventory.model.AssetFacts;
import com.db.alerttriage.triage.entity.TriageDecisionEntity;
import com.db.alerttriage.triage.model.AdvisoryReadRequest;
import com.db.alerttriage.triage.model.TriageAction;
import com.db.alerttriage.triage.model.TriageContext;
import com.db.alerttriage.triage.model.TriageDecision;
import com.db.alerttriage.triage.ai.TriageModel;
import com.db.alerttriage.triage.repository.TriageDecisionRepository;
import com.db.alerttriage.triage.validation.FactsValidator;
import com.db.alerttriage.vulnerability.model.VulnerabilityAdvisory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TriageServiceTest {

    private final TriageContextFactory triageContextFactory =
            mock(TriageContextFactory.class);

    private final TriageModel triageModel =
            mock(TriageModel.class);

    private final FactsValidator factsValidator =
            mock(FactsValidator.class);

    private final TriageDecisionService triageDecisionService =
            mock(TriageDecisionService.class);

    private final TriageDecisionRepository triageDecisionRepository =
            mock(TriageDecisionRepository.class);

    private final TriageService triageService =
            new TriageService(
                    triageContextFactory,
                    triageModel,
                    factsValidator,
                    triageDecisionService,
                    triageDecisionRepository
            );

    @Test
    void returnsReviewWhenModelFails() {

        // ARRANGE
        Alert alert = mock(Alert.class);
        TriageContext context = mock(TriageContext.class);
        AssetFacts asset = mock(AssetFacts.class);
        VulnerabilityAdvisory advisory =
                mock(VulnerabilityAdvisory.class);

        when(triageContextFactory.create(alert))
                .thenReturn(context);

        when(context.cveId())
                .thenReturn("CVE-2026-1234");

        when(context.vulnerabilityAdvisory())
                .thenReturn(advisory);

        when(advisory.advisoryText())
                .thenReturn("Example vendor advisory");

        when(context.assetFacts())
                .thenReturn(asset);

        when(asset.environment())
                .thenReturn(Environment.PROD);

        when(asset.internetExposed())
                .thenReturn(true);

        when(asset.businessCriticality())
                .thenReturn(BusinessCriticality.HIGH);

        when(asset.installedVersion())
                .thenReturn("9.16.5");

        when(asset.ownerTeam())
                .thenReturn("Network Security");

        when(asset.vendor())
                .thenReturn("Cisco");

        when(asset.model())
                .thenReturn("ASA");

        when(triageModel.interpret(any(AdvisoryReadRequest.class)))
                .thenThrow(
                        new RuntimeException("Claude unavailable")
                );

        // ACT
        TriageDecision decision =
                triageService.triage(alert);

        // ASSERT
        assertThat(decision.action())
                .isEqualTo(TriageAction.REVIEW);

        assertThat(decision.reason())
                .isEqualTo(
                        "AI model unavailable - manual review required"
                );

        ArgumentCaptor<TriageDecisionEntity> captor =
                ArgumentCaptor.forClass(
                        TriageDecisionEntity.class
                );

        verify(triageDecisionRepository)
                .save(captor.capture());

        TriageDecisionEntity savedDecision =
                captor.getValue();

        assertThat(savedDecision.getAction())
                .isEqualTo(TriageAction.REVIEW);

        assertThat(savedDecision.getReason())
                .isEqualTo(
                        "AI model unavailable - manual review required"
                );

        assertThat(savedDecision.getAffectedVersionRange())
                .isNull();

        assertThat(savedDecision.getFixedVersion())
                .isNull();

        assertThat(savedDecision.getRemediationSummary())
                .isNull();

        verifyNoInteractions(
                factsValidator,
                triageDecisionService
        );
    }
}