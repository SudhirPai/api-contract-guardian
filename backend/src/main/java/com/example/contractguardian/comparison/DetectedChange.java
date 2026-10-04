package com.example.contractguardian.comparison;


import com.example.contractguardian.persistance.entity.ChangeSeverity;
import com.example.contractguardian.persistance.entity.ChangeType;

public record DetectedChange(ChangeType type, ChangeSeverity severity, String endpoint, String path, String oldValue,
                             String newValue, int confidence) {
}