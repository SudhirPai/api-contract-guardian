package com.example.contractguardian.contract;

import java.util.*;

public record ContractField(String method, String endpoint, String location, String jsonPath, String type,
                            boolean required, boolean nullable, List<String> enums) {
}