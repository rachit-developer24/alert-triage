package com.db.alerttriage.inventory.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hostname", nullable = false, unique = true)
    private String hostname;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false)
    private Environment environment;

    @Column(name = "internet_exposed", nullable = false)
    private boolean internetExposed;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_criticality", nullable = false)
    private BusinessCriticality businessCriticality;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "vendor", nullable = false)
    private String vendor;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "installed_version", nullable = false)
    private String installedVersion;

    @Column(name = "owner_team", nullable = false)
    private String ownerTeam;

    @Column(name = "site", nullable = false)
    private String site;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    protected Asset() {
    }

    public Asset(
            String hostname,
            String ipAddress,
            Environment environment,
            boolean internetExposed,
            BusinessCriticality businessCriticality,
            DeviceType deviceType,
            String vendor,
            String model,
            String installedVersion,
            String ownerTeam,
            String site,
            Instant lastSeenAt
    ) {
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.environment = environment;
        this.internetExposed = internetExposed;
        this.businessCriticality = businessCriticality;
        this.deviceType = deviceType;
        this.vendor = vendor;
        this.model = model;
        this.installedVersion = installedVersion;
        this.ownerTeam = ownerTeam;
        this.site = site;
        this.lastSeenAt = lastSeenAt;
    }

    public Long getId() {
        return id;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIpAddress() {
        return ipAddress;
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

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public String getVendor() {
        return vendor;
    }

    public String getModel() {
        return model;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public String getOwnerTeam() {
        return ownerTeam;
    }

    public String getSite() {
        return site;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }
}