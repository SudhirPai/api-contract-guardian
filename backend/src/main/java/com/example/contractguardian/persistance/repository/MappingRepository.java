package com.example.contractguardian.persistance.repository;

import com.example.contractguardian.persistance.entity.ConsumerMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface MappingRepository extends JpaRepository<ConsumerMapping, Long> {
    List<ConsumerMapping> findByProducerApiId(Long apiId);
}
