package com.ramesh.dataprocessing.processing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingErrorRepository
    extends JpaRepository<ProcessingError, UUID> {
    Page<ProcessingError> findByProcessingJobId(
            UUID processingJobId,
            Pageable pageable
    );
}
