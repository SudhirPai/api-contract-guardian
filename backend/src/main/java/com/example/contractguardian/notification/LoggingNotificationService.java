package com.example.contractguardian.notification;

import com.example.contractguardian.persistance.entity.Impact;
import org.slf4j.*;
import org.springframework.stereotype.Service;

@Service
public class LoggingNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);

    public void notify(Impact i) {
        log.warn("CONTRACT IMPACT level={} score={} reason={}", i.getImpactLevel(), i.getRiskScore(), i.getReason());
    }
}
