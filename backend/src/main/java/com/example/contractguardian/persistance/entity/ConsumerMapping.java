package com.example.contractguardian.persistance.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "consumer_mapping")
public class ConsumerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "consumer_application")
    private String consumerApplication;
    @Column(name = "consumer_method")
    private String consumerMethod;
    @Column(name = "consumer_path")
    private String consumerPath;
    @Column(name = "consumer_field_path")
    private String consumerFieldPath;
    @Column(name = "producer_api_id")
    private Long producerApiId;
    @Column(name = "producer_method")
    private String producerMethod;
    @Column(name = "producer_path")
    private String producerPath;
    @Column(name = "producer_field_path")
    private String producerFieldPath;
    private String transformation;
    private boolean required;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public String getConsumerApplication() {
        return consumerApplication;
    }

    public void setConsumerApplication(String v) {
        consumerApplication = v;
    }

    public String getConsumerMethod() {
        return consumerMethod;
    }

    public void setConsumerMethod(String v) {
        consumerMethod = v;
    }

    public String getConsumerPath() {
        return consumerPath;
    }

    public void setConsumerPath(String v) {
        consumerPath = v;
    }

    public String getConsumerFieldPath() {
        return consumerFieldPath;
    }

    public void setConsumerFieldPath(String v) {
        consumerFieldPath = v;
    }

    public Long getProducerApiId() {
        return producerApiId;
    }

    public void setProducerApiId(Long v) {
        producerApiId = v;
    }

    public String getProducerMethod() {
        return producerMethod;
    }

    public void setProducerMethod(String v) {
        producerMethod = v;
    }

    public String getProducerPath() {
        return producerPath;
    }

    public void setProducerPath(String v) {
        producerPath = v;
    }

    public String getProducerFieldPath() {
        return producerFieldPath;
    }

    public void setProducerFieldPath(String v) {
        producerFieldPath = v;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String v) {
        transformation = v;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean v) {
        required = v;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}