package com.ramesh.dataprocessing.processing;

import com.ramesh.dataprocessing.job.ProcessingJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingJobRepository
    extends JpaRepository<ProcessingJob, UUID> {
}
