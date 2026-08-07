package com.ramesh.dataprocessing.job;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProcessingJobService {

    private final ProcessingJobRepository repository;

    public ProcessingJobService(ProcessingJobRepository repository) {
        this.repository = repository;
    }

    public ProcessingJob createJob(String fileName) {
        ProcessingJob job = ProcessingJob.builder()
                .fileName(fileName)
                .status(JobStatus.UPLOADED)
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(job);
    }
}