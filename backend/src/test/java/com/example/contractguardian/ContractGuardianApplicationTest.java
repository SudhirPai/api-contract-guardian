package com.example.contractguardian;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "guardian.demo-enabled=false")
class ContractGuardianApplicationTest {
    @Test
    void contextLoads() {
    }
}
