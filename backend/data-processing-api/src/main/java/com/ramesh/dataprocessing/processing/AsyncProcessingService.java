package com.ramesh.dataprocessing.processing;

import com.ramesh.dataprocessing.job.JobStatus;
import com.ramesh.dataprocessing.job.ProcessingJob;
import com.ramesh.dataprocessing.job.ProcessingJobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

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
    public void process(UUID jobId, Path filePath) throws IOException {
        ProcessingJob job = processingJobRepository.findById(jobId)
                .orElseThrow();

        job.setStatus(JobStatus.PROCESSING);
        job.setStartedAt(LocalDateTime.now());
        processingJobRepository.save(job);

        ProcessingResult result = csvProcessingService.processFile(filePath);

        job.setProcessedRecords(
                result.successfulRecords() + result.failedRecords()
        );
        job.setSuccessfulRecords(result.successfulRecords());
        job.setFailedRecords(result.failedRecords());
        job.setStatus(result.failedRecords() > 0
                ? JobStatus.COMPLETED_WITH_ERRORS
                : JobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());

        processingJobRepository.save(job);
    }
}
