package com.example.contractguardian.persistance.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "impact")
public class Impact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "contract_change_id")
    private Long contractChangeId;
    @Column(name = "mapping_id")
    private Long mappingId;
    @Enumerated(EnumType.STRING)
    @Column(name = "impact_level")
    private ImpactLevel impactLevel;
    @Column(name = "risk_score")
    private int riskScore;
    private String reason;
    private String status = "OPEN";
    @Column(name = "detected_at")
    private Instant detectedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public Long getContractChangeId() {
        return contractChangeId;
    }

    public void setContractChangeId(Long v) {
        contractChangeId = v;
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long v) {
        mappingId = v;
    }

    public ImpactLevel getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(ImpactLevel v) {
        impactLevel = v;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int v) {
        riskScore = v;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String v) {
        reason = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }
}