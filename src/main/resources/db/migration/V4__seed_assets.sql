INSERT INTO assets (
    hostname,
    ip_address,
    environment,
    internet_exposed,
    business_criticality,
    device_type,
    vendor,
    model,
    installed_version,
    owner_team,
    site,
    last_seen_at
)
VALUES (
    'lon-fw-017',
    '10.20.30.17',
    'PROD',
    true,
    'CRITICAL',
    'FIREWALL',
    'Cisco',
    'ASA-5585',
    '9.16.4',
    'Network Security',
    'London',
    NOW()
);