CREATE UNIQUE INDEX uq_active_alert_cve_hostname
ON alerts (cve_id, hostname)
WHERE status <> 'CLOSED';