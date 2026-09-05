package com.db.alerttriage.triage;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.db.alerttriage.triage.model.AdvisoryFacts;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("fake")
public class AnthropicStructuredOutputSpike {

    public static void main(String[] args) {

        AnthropicClient client =
                AnthropicOkHttpClient.fromEnv();

        StructuredMessageCreateParams<AdvisoryFacts> params =
                MessageCreateParams.builder()
                        .model(Model.CLAUDE_SONNET_4_6)
                        .maxTokens(1500)
                        .outputConfig(AdvisoryFacts.class)
                        .addUserMessage("""
                                Extract vulnerability facts from this advisory.

                                Rules:
                                - Use only information explicitly present in the advisory.
                                - Do not guess.
                                - Evidence quotes must come from the advisory.
                                - If a fact is missing, leave it missing rather than inventing it.

                                Advisory:

                                Versions before 9.16.6 are affected.
                                Customers should upgrade to 9.16.6 or later.
                                Successful exploitation requires network access
                                to the management interface.
                                Restrict management access to trusted networks
                                to reduce exposure.
                                """)
                        .build();

        AdvisoryFacts facts =
                client.messages()
                        .create(params)
                        .content()
                        .stream()
                        .flatMap(block -> block.text().stream())
                        .findFirst()
                        .orElseThrow()
                        .text();

        System.out.println(facts);
    }
}