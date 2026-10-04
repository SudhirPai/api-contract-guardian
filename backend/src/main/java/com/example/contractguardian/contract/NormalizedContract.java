package com.example.contractguardian.contract;

import java.util.*;

public record NormalizedContract(List<String> endpoints, List<ContractField> fields) {
}