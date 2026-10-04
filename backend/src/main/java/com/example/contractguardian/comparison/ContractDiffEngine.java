package com.example.contractguardian.comparison;

import com.example.contractguardian.contract.ContractField;
import com.example.contractguardian.contract.NormalizedContract;
import com.example.contractguardian.persistance.entity.ChangeSeverity;
import com.example.contractguardian.persistance.entity.ChangeType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ContractDiffEngine {
    private final RenameDetector renames;

    public ContractDiffEngine(RenameDetector r) {
        renames = r;
    }

    public List<DetectedChange> compare(NormalizedContract oldC, NormalizedContract newC) {
        List<DetectedChange> result = new ArrayList<>();
        Set<String> oldEndpoints = new HashSet<>(oldC.endpoints()), newEndpoints = new HashSet<>(newC.endpoints());
        oldEndpoints.stream().filter(e -> !newEndpoints.contains(e)).forEach(e -> result.add(new DetectedChange(ChangeType.ENDPOINT_REMOVED, ChangeSeverity.BREAKING, e, null, null, null, 100)));
        newEndpoints.stream().filter(e -> !oldEndpoints.contains(e)).forEach(e -> result.add(new DetectedChange(ChangeType.ENDPOINT_ADDED, ChangeSeverity.NON_BREAKING, e, null, null, null, 100)));
        Map<String, ContractField> old = fields(oldC), ne = fields(newC);
        List<ContractField> removed = old.keySet().stream().filter(k -> !ne.containsKey(k)).map(old::get).toList();
        List<ContractField> added = ne.keySet().stream().filter(k -> !old.containsKey(k)).map(ne::get).toList();
        Set<ContractField> renamedOld = new HashSet<>(), renamedNew = new HashSet<>();
        for (ContractField o : removed)
            for (ContractField n : added)
                if (o.method().equals(n.method()) && o.endpoint().equals(n.endpoint()) && o.type().equals(n.type())) {
                    int c = renames.confidence(o.jsonPath(), n.jsonPath());
                    if (c >= 90) {
                        result.add(new DetectedChange(ChangeType.LIKELY_FIELD_RENAME, ChangeSeverity.BREAKING, o.method() + " " + o.endpoint(), o.jsonPath(), o.jsonPath(), n.jsonPath(), c));
                        renamedOld.add(o);
                        renamedNew.add(n);
                        break;
                    }
                }
        for (ContractField f : removed)
            if (!renamedOld.contains(f))
                result.add(new DetectedChange(ChangeType.RESPONSE_FIELD_REMOVED, ChangeSeverity.BREAKING, f.method() + " " + f.endpoint(), f.jsonPath(), value(f), null, 100));
        for (ContractField f : added)
            if (!renamedNew.contains(f))
                result.add(new DetectedChange(ChangeType.RESPONSE_FIELD_ADDED, f.required() ? ChangeSeverity.POTENTIALLY_BREAKING : ChangeSeverity.NON_BREAKING, f.method() + " " + f.endpoint(), f.jsonPath(), null, value(f), 100));
        old.forEach((k, o) -> {
            ContractField n = ne.get(k);
            if (n == null) return;
            String ep = o.method() + " " + o.endpoint();
            if (!o.type().equals(n.type()))
                result.add(new DetectedChange(ChangeType.FIELD_TYPE_CHANGED, ChangeSeverity.BREAKING, ep, o.jsonPath(), o.type(), n.type(), 100));
            if (!o.required() && n.required())
                result.add(new DetectedChange(ChangeType.OPTIONAL_TO_REQUIRED, ChangeSeverity.POTENTIALLY_BREAKING, ep, o.jsonPath(), "optional", "required", 100));
            if (o.required() && !n.required())
                result.add(new DetectedChange(ChangeType.REQUIRED_TO_OPTIONAL, ChangeSeverity.NON_BREAKING, ep, o.jsonPath(), "required", "optional", 100));
            if (o.nullable() != n.nullable())
                result.add(new DetectedChange(ChangeType.NULLABLE_CHANGED, n.nullable() ? ChangeSeverity.NON_BREAKING : ChangeSeverity.POTENTIALLY_BREAKING, ep, o.jsonPath(), String.valueOf(o.nullable()), String.valueOf(n.nullable()), 100));
            if (!o.enums().equals(n.enums()))
                result.add(new DetectedChange(ChangeType.ENUM_CHANGED, ChangeSeverity.POTENTIALLY_BREAKING, ep, o.jsonPath(), o.enums().toString(), n.enums().toString(), 100));
        });
        return result;
    }

    private Map<String, ContractField> fields(NormalizedContract c) {
        Map<String, ContractField> m = new HashMap<>();
        c.fields().forEach(f -> m.put(f.method() + "|" + f.endpoint() + "|" + f.location() + "|" + f.jsonPath(), f));
        return m;
    }

    private String value(ContractField f) {
        return f.type() + " (" + (f.required() ? "required" : "optional") + ")";
    }
}