package com.db.alerttriage.triage.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.db.alerttriage.triage.ai.TriageModel;
import com.db.alerttriage.triage.model.AdvisoryFacts;
import com.db.alerttriage.triage.model.AdvisoryReadRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("anthropic")
public class AnthropicTriageModel implements TriageModel {

    private final AnthropicClient client;

    public AnthropicTriageModel() {
        this.client = AnthropicOkHttpClient.fromEnv();
    }

    @Override
    public AdvisoryFacts interpret(AdvisoryReadRequest request) {

        String prompt = """
                You are a vulnerability advisory extraction service.

                Extract facts ONLY from the advisory text provided below.

                Rules:
                - Do not invent information.
                - If a fact is not present in the advisory, use an empty string or empty list.
                - Evidence quotes must be copied exactly from the advisory text.
                - Do not use outside knowledge.
                - Do not decide severity, priority, SLA, or remediation action.
                - Only extract information from the supplied advisory.

                CVE:
                %s

                ADVISORY:
                %s
                """.formatted(
                request.cveId(),
                request.advisoryText()
        );

        StructuredMessageCreateParams<AdvisoryFacts> params =
                MessageCreateParams.builder()
                        .model(Model.CLAUDE_SONNET_5)
                        .maxTokens(1200)
                        .outputConfig(AdvisoryFacts.class)
                        .addUserMessage(prompt)
                        .build();

        return client.messages()
                .create(params)
                .content()
                .stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Claude returned no structured response"))
                .text();
    }
}