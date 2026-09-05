package com.db.alerttriage.triage.ai;

import com.db.alerttriage.triage.model.AdvisoryFacts;
import com.db.alerttriage.triage.model.AdvisoryReadRequest;


public interface TriageModel {
    AdvisoryFacts interpret(AdvisoryReadRequest request);
}
