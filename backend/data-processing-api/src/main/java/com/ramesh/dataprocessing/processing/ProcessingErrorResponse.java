package com.ramesh.dataprocessing.processing;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProcessingErrorResponse(
        UUID id,
        long rowNumber,
        String rawData,
        String errorMessage,
        LocalDateTime createdAt
) {}