package com.example.contractguardian.api;

import com.example.contractguardian.persistance.entity.Environment;
import jakarta.validation.constraints.*;

public record ApiRequest(@NotBlank String name, @NotBlank String application, @NotBlank String team,
                         @NotNull Environment environment, @NotBlank String swaggerUrl, String description,
                         Boolean pollingEnabled) {
}