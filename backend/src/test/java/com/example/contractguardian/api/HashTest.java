package com.example.contractguardian.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTest {
    @Test
    void hashesDeterministically() {
        assertEquals(ApiService.sha256("x"), ApiService.sha256("x"));
        assertNotEquals(ApiService.sha256("x"), ApiService.sha256("y"));
    }
}
