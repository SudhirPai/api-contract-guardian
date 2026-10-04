package com.example.contractguardian.common;

import com.example.contractguardian.api.*;
import com.example.contractguardian.mapping.*;
import com.example.contractguardian.persistance.entity.Environment;
import com.example.contractguardian.persistance.repository.ApiRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "guardian.demo-enabled", havingValue = "true", matchIfMissing = true)
public class DemoData implements CommandLineRunner {
    private final ApiRepository apis;
    private final ApiService apiService;
    private final MappingService mappings;

    public DemoData(ApiRepository a, ApiService s, MappingService m) {
        apis = a;
        apiService = s;
        mappings = m;
    }

    public void run(String... x) {
        if (apis.count() > 0) return;
        var api = apiService.create(new ApiRequest("Customer API", "data-api", "data-team", Environment.QA, "classpath:fixtures/customer-v1.yaml", "Local, no-network demonstration contract", true));
        Long id = api.getId();
        mappings.create(new MappingRequest("User App", "GET", "/profile", "$.name", id, "GET", "/customer", "$.customer.user_name", "DIRECT", true));
        mappings.create(new MappingRequest("User App", "GET", "/account-summary", "$.name", id, "GET", "/customer", "$.customer.user_name", "DIRECT", true));
        mappings.create(new MappingRequest("Payment App", "GET", "/customer-details", "$.customerName", id, "GET", "/customer", "$.customer.user_name", "DIRECT", true));
    }
}