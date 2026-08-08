package com.ramesh.dataprocessing.job;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProcessingJobResponse(
        UUID id,
        String fileName,
        JobStatus status,
        LocalDateTime createdAt
) {
}