package com.example.contractguardian.api;

import com.example.contractguardian.persistance.entity.ApiContract;
import com.example.contractguardian.persistance.entity.ContractChange;
import com.example.contractguardian.persistance.entity.Impact;
import com.example.contractguardian.persistance.entity.MonitoredApi;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/apis")
public class ApiController {
    private final ApiService service;

    public ApiController(ApiService s) {
        service = s;
    }

    @PostMapping
    public ResponseEntity<MonitoredApi> create(@Valid @RequestBody ApiRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
    }

    @GetMapping
    public List<MonitoredApi> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public MonitoredApi get(@PathVariable Long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/check")
    public ApiService.CheckResult check(@PathVariable Long id) {
        return service.check(id);
    }


    @PostMapping("/{id}/demo/v2")
    public ApiService.CheckResult demoV2(@PathVariable Long id) {
        return service.demoV2(id);
    }

    @GetMapping("/{id}/contracts")
    public List<ApiContract> contracts(@PathVariable Long id) {
        return service.contracts(id);
    }

    @GetMapping("/{id}/changes")
    public List<ContractChange> changes(@PathVariable Long id) {
        return service.changes(id);
    }

    @GetMapping("/{id}/impacts")
    public List<Impact> impacts(@PathVariable Long id) {
        return service.impacts(id);
    }

}