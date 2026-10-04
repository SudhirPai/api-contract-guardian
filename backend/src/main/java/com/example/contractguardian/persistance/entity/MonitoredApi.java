package com.example.contractguardian.persistance.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "monitored_api")
public class MonitoredApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "application")
    private String application;
    private String team;
    @Enumerated(EnumType.STRING)
    private Environment environment;
    @Column(name = "swagger_url")
    private String swaggerUrl;
    private String description;
    @Column(name = "polling_enabled")
    private boolean pollingEnabled = true;
    @Column(name = "current_contract_id")
    private Long currentContractId;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String v) {
        application = v;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String v) {
        team = v;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment v) {
        environment = v;
    }

    public String getSwaggerUrl() {
        return swaggerUrl;
    }

    public void setSwaggerUrl(String v) {
        swaggerUrl = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        description = v;
    }

    public boolean isPollingEnabled() {
        return pollingEnabled;
    }

    public void setPollingEnabled(boolean v) {
        pollingEnabled = v;
    }

    public Long getCurrentContractId() {
        return currentContractId;
    }

    public void setCurrentContractId(Long v) {
        currentContractId = v;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}