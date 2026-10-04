package com.example.contractguardian.impact;

import com.example.contractguardian.persistance.entity.ContractChange;
import com.example.contractguardian.persistance.entity.Impact;
import com.example.contractguardian.persistance.repository.ChangeRepository;
import com.example.contractguardian.persistance.repository.ImpactRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ImpactController {
    private final ImpactRepository impacts;
    private final ChangeRepository changes;

    public ImpactController(ImpactRepository i, ChangeRepository c) {
        impacts = i;
        changes = c;
    }

    @GetMapping("/impacts")
    public List<Impact> impacts() {
        return impacts.findTop50ByOrderByDetectedAtDesc();
    }

    @GetMapping("/changes/{id}")
    public ContractChange change(@PathVariable Long id) {
        return changes.findById(id).orElseThrow(() -> new NoSuchElementException("Change not found"));
    }
}