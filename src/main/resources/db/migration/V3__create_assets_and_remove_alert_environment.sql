ALTER TABLE alerts
DROP COLUMN environment;

CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,

    hostname VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(45) NOT NULL,

    environment VARCHAR(20) NOT NULL,
    internet_exposed BOOLEAN NOT NULL,

    business_criticality VARCHAR(20) NOT NULL,
    device_type VARCHAR(30) NOT NULL,

    vendor VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    installed_version VARCHAR(100) NOT NULL,

    owner_team VARCHAR(100) NOT NULL,
    site VARCHAR(100) NOT NULL,

    last_seen_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_assets_environment
        CHECK (environment IN ('DEV', 'TEST', 'STAGING', 'PROD')),

    CONSTRAINT chk_assets_business_criticality
        CHECK (business_criticality IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),

    CONSTRAINT chk_assets_device_type
        CHECK (device_type IN ('FIREWALL', 'ROUTER', 'SWITCH', 'LOAD_BALANCER', 'OTHER'))
);