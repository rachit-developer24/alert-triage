CREATE TABLE triage_decisions (
    id BIGSERIAL PRIMARY KEY,

    alert_id BIGINT NOT NULL,

    action VARCHAR(30) NOT NULL,
    reason TEXT NOT NULL,

    environment VARCHAR(20) NOT NULL,
    internet_exposed BOOLEAN NOT NULL,
    business_criticality VARCHAR(20) NOT NULL,

    installed_version VARCHAR(100) NOT NULL,
    owner_team VARCHAR(100) NOT NULL,

    device_vendor VARCHAR(100) NOT NULL,
    device_model VARCHAR(100) NOT NULL,

    affected_version_range VARCHAR(255),
    fixed_version VARCHAR(100),
    remediation_summary TEXT,

    decided_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_triage_decision_alert
        FOREIGN KEY (alert_id)
        REFERENCES alerts(id),

    CONSTRAINT chk_triage_action
        CHECK (action IN ('ESCALATE', 'REVIEW', 'AUTO_CLOSE'))
);

CREATE INDEX idx_triage_decisions_alert_id
    ON triage_decisions(alert_id);