package com.ramesh.dataprocessing.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingJobRepository
        extends JpaRepository<ProcessingJob, UUID> {
    Page<ProcessingJob> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
