package com.ramesh.dataprocessing.job;

import com.ramesh.dataprocessing.processing.CsvProcessingService;
import com.ramesh.dataprocessing.processing.ProcessingResult;
import com.ramesh.dataprocessing.storage.LocalFileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ProcessingJobService {

    private final ProcessingJobRepository repository;
    private final LocalFileStorageService fileStorageService;
    private final CsvProcessingService csvProcessingService;

    public ProcessingJobService(
            ProcessingJobRepository repository,
            LocalFileStorageService fileStorageService,
            CsvProcessingService csvProcessingService
    ) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.csvProcessingService = csvProcessingService;
    }

    public ProcessingJob createJob(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        Path storedFile = fileStorageService.store(file);

        ProcessingResult result = csvProcessingService.processFile(storedFile);

        long totalRows = csvProcessingService.countRows(storedFile);

        long processedRows = result.successfulRecords() + result.failedRecords();

        ProcessingJob job = ProcessingJob.builder()
                .fileName(file.getOriginalFilename())
                .status(
                        result.failedRecords() > 0
                                ? JobStatus.COMPLETED_WITH_ERRORS
                                : JobStatus.COMPLETED
                )
                .totalRecords(totalRows)
                .processedRecords(processedRows)
                .successfulRecords(result.successfulRecords())
                .failedRecords(result.failedRecords())
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

//        long totalRecords = csvProcessingService.countRows(storedFile);
//
//        ProcessingJob job = ProcessingJob.builder()
//                .fileName(file.getOriginalFilename())
//                .status(JobStatus.UPLOADED)
//                .totalRecords(totalRecords)
//                .createdAt(LocalDateTime.now())
//                .build();

        return repository.save(job);
    }
}