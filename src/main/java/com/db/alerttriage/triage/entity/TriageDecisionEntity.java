package com.db.alerttriage.triage.entity;

import com.db.alerttriage.alert.entity.Alert;
import com.db.alerttriage.inventory.entity.BusinessCriticality;
import com.db.alerttriage.inventory.entity.Environment;
import com.db.alerttriage.triage.model.ReviewStatus;
import com.db.alerttriage.triage.model.TriageAction;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "triage_decisions")
public class TriageDecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private TriageAction action;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false)
    private Environment environment;

    @Column(name = "internet_exposed", nullable = false)
    private boolean internetExposed;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_criticality", nullable = false)
    private BusinessCriticality businessCriticality;

    @Column(name = "installed_version", nullable = false)
    private String installedVersion;

    @Column(name = "owner_team", nullable = false)
    private String ownerTeam;

    @Column(name = "device_vendor", nullable = false)
    private String deviceVendor;

    @Column(name = "device_model", nullable = false)
    private String deviceModel;

    @Column(name = "affected_version_range")
    private String affectedVersionRange;

    @Column(name = "fixed_version")
    private String fixedVersion;

    @Column(name = "remediation_summary")
    private String remediationSummary;

    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    private ReviewStatus reviewStatus = ReviewStatus.PENDING;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "review_comment", length = 1000)
    private String reviewComment;

    protected TriageDecisionEntity() {
    }

    public TriageDecisionEntity(
            Alert alert,
            TriageAction action,
            String reason,
            Environment environment,
            boolean internetExposed,
            BusinessCriticality businessCriticality,
            String installedVersion,
            String ownerTeam,
            String deviceVendor,
            String deviceModel,
            String affectedVersionRange,
            String fixedVersion,
            String remediationSummary,
            Instant decidedAt
    ) {
        this.alert = alert;
        this.action = action;
        this.reason = reason;
        this.environment = environment;
        this.internetExposed = internetExposed;
        this.businessCriticality = businessCriticality;
        this.installedVersion = installedVersion;
        this.ownerTeam = ownerTeam;
        this.deviceVendor = deviceVendor;
        this.deviceModel = deviceModel;
        this.affectedVersionRange = affectedVersionRange;
        this.fixedVersion = fixedVersion;
        this.remediationSummary = remediationSummary;
        this.decidedAt = decidedAt;
        this.reviewStatus = ReviewStatus.PENDING;
    }

    public void approve(
            String reviewedBy,
            String reviewComment,
            Instant reviewedAt
    ) {
        ensurePending();

        this.reviewStatus = ReviewStatus.APPROVED;
        this.reviewedBy = reviewedBy;
        this.reviewComment = reviewComment;
        this.reviewedAt = reviewedAt;
    }

    public void reject(
            String reviewedBy,
            String reviewComment,
            Instant reviewedAt
    ) {
        ensurePending();

        this.reviewStatus = ReviewStatus.REJECTED;
        this.reviewedBy = reviewedBy;
        this.reviewComment = reviewComment;
        this.reviewedAt = reviewedAt;
    }

    private void ensurePending() {
        if (reviewStatus != ReviewStatus.PENDING) {
            throw new IllegalStateException(
                    "Triage decision has already been reviewed"
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Alert getAlert() {
        return alert;
    }

    public TriageAction getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean isInternetExposed() {
        return internetExposed;
    }

    public BusinessCriticality getBusinessCriticality() {
        return businessCriticality;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public String getOwnerTeam() {
        return ownerTeam;
    }

    public String getDeviceVendor() {
        return deviceVendor;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getAffectedVersionRange() {
        return affectedVersionRange;
    }

    public String getFixedVersion() {
        return fixedVersion;
    }

    public String getRemediationSummary() {
        return remediationSummary;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public String getReviewComment() {
        return reviewComment;
    }
}