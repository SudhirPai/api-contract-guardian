package com.example.contractguardian.persistance.repository;

import com.example.contractguardian.persistance.entity.ApiContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ContractRepository extends JpaRepository<ApiContract, Long> {
    List<ApiContract> findByApiIdOrderByDetectedAtDesc(Long apiId);

    Optional<ApiContract> findFirstByApiIdOrderByDetectedAtDesc(Long apiId);

    long countByApiId(Long apiId);
}
