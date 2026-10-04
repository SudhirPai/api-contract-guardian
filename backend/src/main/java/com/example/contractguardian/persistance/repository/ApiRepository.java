package com.example.contractguardian.persistance.repository;

import com.example.contractguardian.persistance.entity.MonitoredApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRepository extends JpaRepository<MonitoredApi, Long> {
}
