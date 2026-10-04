package com.example.contractguardian.scheduler;

import com.example.contractguardian.api.ApiService;
import com.example.contractguardian.persistance.repository.ApiRepository;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContractPollingJob {
    private static final Logger log = LoggerFactory.getLogger(ContractPollingJob.class);
    private final ApiRepository apis;
    private final ApiService service;

    public ContractPollingJob(ApiRepository a, ApiService s) {
        apis = a;
        service = s;
    }

    @Scheduled(fixedDelayString = "${guardian.polling-interval-ms}")
    public void poll() {
        apis.findAll().stream().filter(a -> a.isPollingEnabled()).forEach(a -> {
            try {
                service.check(a.getId());
            } catch (Exception e) {
                log.warn("Polling failed for API {}", a.getId(), e);
            }
        });
    }
}