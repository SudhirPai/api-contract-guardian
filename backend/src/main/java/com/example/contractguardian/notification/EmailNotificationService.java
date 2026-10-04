package com.example.contractguardian.notification;

import com.example.contractguardian.persistance.entity.Impact;
import org.slf4j.*;
import org.springframework.stereotype.Component;

/**
 * Extension point; real mail transport intentionally remains outside this MVP.
 */
@Component
public class EmailNotificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public void queue(Impact impact) {
        log.info("Email notification stub queued for impact {}", impact.getId());
    }
}
