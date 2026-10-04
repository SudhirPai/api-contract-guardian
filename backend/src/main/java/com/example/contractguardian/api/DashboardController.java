package com.example.contractguardian.api;

import com.example.contractguardian.persistance.entity.ConsumerMapping;
import com.example.contractguardian.persistance.entity.ImpactLevel;
import com.example.contractguardian.persistance.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ApiRepository apis;
    private final ContractRepository contracts;
    private final ImpactRepository impacts;
    private final ChangeRepository changes;
    private final MappingRepository mappings;

    public DashboardController(ApiRepository a, ContractRepository c, ImpactRepository i, ChangeRepository ch, MappingRepository m) {
        apis = a;
        contracts = c;
        impacts = i;
        changes = ch;
        mappings = m;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        long high = impacts.findTop50ByOrderByDetectedAtDesc().stream().filter(i -> i.getImpactLevel() == ImpactLevel.HIGH || i.getImpactLevel() == ImpactLevel.CRITICAL).count();
        return Map.of("monitoredApis", apis.count(), "contracts", contracts.count(), "highCriticalImpacts", high, "unresolvedImpacts", impacts.countByStatus("OPEN"), "totalConsumers", mappings.findAll().stream().map(ConsumerMapping::getConsumerApplication).distinct().count(), "recentChanges", changes.findTop20ByOrderByDetectedAtDesc());
    }
}