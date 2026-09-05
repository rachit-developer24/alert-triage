package com.db.alerttriage.alert.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_alert_id", nullable = false)
    private String sourceAlertId;

    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "hostname", nullable = false)
    private String hostname;

    @Column(name = "cvss_score", nullable = false)
    private BigDecimal cvssScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AlertStatus status;

    protected Alert() {
    }

    public Alert(
            String sourceAlertId,
            String cveId,
            String hostname,
            BigDecimal cvssScore,
            Severity severity,
            String description,
            Instant detectedAt,
            Instant receivedAt,
            AlertStatus status
    ) {
        this.sourceAlertId = sourceAlertId;
        this.cveId = cveId;
        this.hostname = hostname;
        this.cvssScore = cvssScore;
        this.severity = severity;
        this.description = description;
        this.detectedAt = detectedAt;
        this.receivedAt = receivedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getSourceAlertId() {
        return sourceAlertId;
    }

    public String getCveId() {
        return cveId;
    }

    public String getHostname() {
        return hostname;
    }

    public BigDecimal getCvssScore() {
        return cvssScore;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public AlertStatus getStatus() {
        return status;
    }
}