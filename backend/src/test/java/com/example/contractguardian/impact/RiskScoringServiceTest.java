package com.example.contractguardian.impact;

import com.example.contractguardian.persistance.entity.ChangeType;
import com.example.contractguardian.persistance.entity.Environment;
import com.example.contractguardian.persistance.entity.ImpactLevel;
import org.junit.jupiter.api.Test;

import static com.example.contractguardian.persistance.entity.ChangeSeverity.BREAKING;
import static org.junit.jupiter.api.Assertions.assertEquals;

;

class RiskScoringServiceTest {
    @Test
    void scoresAndClamps() {
        var r = new RiskScoringService();
        assertEquals(95, r.score(BREAKING, true, Environment.INT, 1, 100, ChangeType.FIELD_TYPE_CHANGED));
        assertEquals(ImpactLevel.HIGH, r.level(80));
        assertEquals(ImpactLevel.CRITICAL, r.level(90));
    }
}
