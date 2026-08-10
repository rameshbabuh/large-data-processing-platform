package com.ramesh.dataprocessing.job;

import com.ramesh.dataprocessing.processing.AsyncProcessingService;
import com.ramesh.dataprocessing.processing.CsvProcessingService;
import com.ramesh.dataprocessing.storage.LocalFileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProcessingJobService {

    private final ProcessingJobRepository repository;
    private final LocalFileStorageService fileStorageService;
    private final CsvProcessingService csvProcessingService;
    private final AsyncProcessingService asyncProcessingService;

    public ProcessingJobService(
            ProcessingJobRepository repository,
            LocalFileStorageService fileStorageService,
            CsvProcessingService csvProcessingService,
            AsyncProcessingService asyncProcessingService
    ) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.csvProcessingService = csvProcessingService;
        this.asyncProcessingService = asyncProcessingService;
    }

    public ProcessingJob createJob(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        Path storedFile = fileStorageService.store(file);
        long totalRows = csvProcessingService.countRows(storedFile);

        ProcessingJob job = ProcessingJob.builder()
                .fileName(file.getOriginalFilename())
                .status(JobStatus.QUEUED)
                .totalRecords(totalRows)
                .processedRecords(0)
                .successfulRecords(0)
                .failedRecords(0)
                .createdAt(LocalDateTime.now())
                .build();

        job = repository.save(job);

        asyncProcessingService.process(job.getId(), storedFile);

        return job;

//        long totalRecords = csvProcessingService.countRows(storedFile);
//
//        ProcessingJob job = ProcessingJob.builder()
//                .fileName(file.getOriginalFilename())
//                .status(JobStatus.UPLOADED)
//                .totalRecords(totalRecords)
//                .createdAt(LocalDateTime.now())
//                .build();

//        return repository.save(job);
    }

    public ProcessingJob getJob(UUID jobId) {
        return repository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }

    public Page<ProcessingJob> getAllJobs(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                )
        );
    }
}