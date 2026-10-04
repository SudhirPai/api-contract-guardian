package com.example.contractguardian.persistance.repository;

import com.example.contractguardian.persistance.entity.ContractChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ChangeRepository extends JpaRepository<ContractChange, Long> {
    List<ContractChange> findByNewContractIdOrderByDetectedAtDesc(Long id);

    List<ContractChange> findTop20ByOrderByDetectedAtDesc();
}
