package com.example.contractguardian.persistance.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "contract_change")
public class ContractChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "old_contract_id")
    private Long oldContractId;
    @Column(name = "new_contract_id")
    private Long newContractId;
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    private ChangeType changeType;
    @Enumerated(EnumType.STRING)
    private ChangeSeverity severity;
    private String endpoint;
    @Column(name = "json_path")
    private String jsonPath;
    @Column(name = "old_value", columnDefinition = "text")
    private String oldValue;
    @Column(name = "new_value", columnDefinition = "text")
    private String newValue;
    private int confidence;
    @Column(name = "detected_at")
    private Instant detectedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public Long getOldContractId() {
        return oldContractId;
    }

    public void setOldContractId(Long v) {
        oldContractId = v;
    }

    public Long getNewContractId() {
        return newContractId;
    }

    public void setNewContractId(Long v) {
        newContractId = v;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType v) {
        changeType = v;
    }

    public ChangeSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ChangeSeverity v) {
        severity = v;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String v) {
        endpoint = v;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String v) {
        jsonPath = v;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String v) {
        oldValue = v;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String v) {
        newValue = v;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int v) {
        confidence = v;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }
}