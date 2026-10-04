package com.example.contractguardian.comparison;

import com.example.contractguardian.contract.ContractField;
import com.example.contractguardian.contract.NormalizedContract;
import com.example.contractguardian.persistance.entity.ChangeType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractDiffEngineTest {
    private final ContractDiffEngine engine = new ContractDiffEngine(new RenameDetector());

    @Test
    void detectsCasingRenameAndTypeChange() {
        var oldC = new NormalizedContract(List.of("GET /customer"), List.of(new ContractField("GET", "/customer", "response", "$.customer.user_name", "string", true, false, List.of()), new ContractField("GET", "/customer", "response", "$.customer.age", "integer", false, false, List.of())));
        var newC = new NormalizedContract(List.of("GET /customer"), List.of(new ContractField("GET", "/customer", "response", "$.customer.userName", "string", true, false, List.of()), new ContractField("GET", "/customer", "response", "$.customer.age", "number", false, false, List.of())));
        var changes = engine.compare(oldC, newC);
        assertTrue(changes.stream().anyMatch(c -> c.type() == ChangeType.LIKELY_FIELD_RENAME && c.confidence() >= 90));
        assertTrue(changes.stream().anyMatch(c -> c.type() == ChangeType.FIELD_TYPE_CHANGED));
    }

    @Test
    void canonicalizesIdentifierVariants() {
        var d = new RenameDetector();
        assertEquals(d.canonical("user_name"), d.canonical("UserName"));
        assertEquals(98, d.confidence("$.address_line_1", "$.addressLine1"));
    }
}
