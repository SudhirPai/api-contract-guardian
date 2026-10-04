package com.example.contractguardian.persistance.repository;

import com.example.contractguardian.persistance.entity.Impact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ImpactRepository extends JpaRepository<Impact, Long> {
    List<Impact> findByContractChangeId(Long id);

    List<Impact> findTop50ByOrderByDetectedAtDesc();

    long countByStatus(String status);
}