package com.ramesh.dataprocessing.processing;

import com.ramesh.dataprocessing.job.JobStatus;
import com.ramesh.dataprocessing.job.ProcessingJob;
import com.ramesh.dataprocessing.job.ProcessingJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AsyncProcessingService {

    private final CsvProcessingService csvProcessingService;
    private final ProcessingJobRepository processingJobRepository;

    public AsyncProcessingService(
            CsvProcessingService csvProcessingService,
            ProcessingJobRepository processingJobRepository) {
        this.csvProcessingService = csvProcessingService;
        this.processingJobRepository = processingJobRepository;
    }

    @Async
    public void process(UUID jobId, Path filePath) {
        ProcessingJob job = processingJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        try {
            job.setStatus(JobStatus.PROCESSING);
            job.setStartedAt(LocalDateTime.now());
            processingJobRepository.save(job);

            long startTime = System.currentTimeMillis();

            ProcessingResult result =
                    csvProcessingService.processFile(jobId, filePath);

            long duration = System.currentTimeMillis() - startTime;

            log.info(
                    "Job {} processed {} records in {} ms",
                    jobId,
                    result.successfulRecords() + result.failedRecords(),
                    duration
            );

            long totalProcessed =
                    result.successfulRecords() + result.failedRecords();

            job.setTotalRecords(totalProcessed);
            job.setProcessedRecords(totalProcessed);
            job.setSuccessfulRecords(result.successfulRecords());
            job.setFailedRecords(result.failedRecords());

            job.setStatus(
                    result.failedRecords() > 0
                            ? JobStatus.COMPLETED_WITH_ERRORS
                            : JobStatus.COMPLETED
            );

            job.setCompletedAt(LocalDateTime.now());
            processingJobRepository.save(job);

        } catch (Exception ex) {
            job.setStatus(JobStatus.FAILED);
            job.setCompletedAt(LocalDateTime.now());
            processingJobRepository.save(job);

            log.error("Processing failed for job {}", jobId, ex);
        }
    }
}
