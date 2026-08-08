package com.ramesh.dataprocessing.processing;

public record ProcessingResult(
        long successfulRecords,
        long failedRecords
) {
}