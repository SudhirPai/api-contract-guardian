package com.example.contractguardian.impact;

import com.example.contractguardian.persistance.entity.ChangeSeverity;
import com.example.contractguardian.persistance.entity.ChangeType;
import com.example.contractguardian.persistance.entity.Environment;
import com.example.contractguardian.persistance.entity.ImpactLevel;
import org.springframework.stereotype.Service;

/**
 * Deterministic MVP rules: base 80/50/10, +10 mandatory, +5 PROD, +5 when >3 consumers, -10 for high-confidence rename; clamped to 0..100.
 */
@Service
public class RiskScoringService {
    public int score(ChangeSeverity severity, boolean mandatory,Environment environment, int consumerCount, int confidence, ChangeType type) {
        int s = switch (severity) {
            case BREAKING -> 80;
            case POTENTIALLY_BREAKING -> 50;
            case NON_BREAKING -> 10;
        };
        if (mandatory) s += 10;
        if (environment == Environment.INT) s += 5;
        if (consumerCount > 3) s += 5;
        if (type == ChangeType.LIKELY_FIELD_RENAME && confidence >= 90) s -= 10;
        return Math.max(0, Math.min(100, s));
    }

    public ImpactLevel level(int s) {
        return s >= 90 ? ImpactLevel.CRITICAL : s >= 70 ? ImpactLevel.HIGH : s >= 40 ? ImpactLevel.MEDIUM : ImpactLevel.LOW;
    }
}