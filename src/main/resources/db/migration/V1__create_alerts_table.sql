CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    source_alert_id VARCHAR(100) NOT NULL,
    cve_id VARCHAR(50) NOT NULL,
    hostname VARCHAR(255) NOT NULL,
    cvss_score NUMERIC(3,1) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    environment VARCHAR(20),
    description TEXT NOT NULL,
    detected_at TIMESTAMP WITH TIME ZONE NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL
);