package com.example.contractguardian.mapping;

import jakarta.validation.constraints.*;

public record MappingRequest(@NotBlank String consumerApplication, @NotBlank String consumerMethod,
                             @NotBlank String consumerPath, @NotBlank String consumerFieldPath,
                             @NotNull Long producerApiId, @NotBlank String producerMethod,
                             @NotBlank String producerPath, @NotBlank String producerFieldPath, String transformation,
                             boolean required) {
}
