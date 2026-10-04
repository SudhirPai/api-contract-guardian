package com.example.contractguardian.persistance.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "api_contract")
public class ApiContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "api_id")
    private Long apiId;
    private String version;
    private String hash;
    @Column(name = "raw_document", columnDefinition = "text")
    private String rawDocument;
    @Column(name = "normalized_document", columnDefinition = "text")
    private String normalizedDocument;
    @Column(name = "detected_at")
    private Instant detectedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long v) {
        apiId = v;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String v) {
        version = v;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String v) {
        hash = v;
    }

    public String getRawDocument() {
        return rawDocument;
    }

    public void setRawDocument(String v) {
        rawDocument = v;
    }

    public String getNormalizedDocument() {
        return normalizedDocument;
    }

    public void setNormalizedDocument(String v) {
        normalizedDocument = v;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }
}