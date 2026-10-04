package com.example.contractguardian.notification;


import com.example.contractguardian.persistance.entity.Impact;

public interface NotificationService {
    void notify(Impact impact);
}
