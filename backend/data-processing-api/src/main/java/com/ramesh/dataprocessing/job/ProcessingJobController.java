package com.ramesh.dataprocessing.job;

import com.ramesh.dataprocessing.processing.ProcessingError;
import com.ramesh.dataprocessing.processing.ProcessingErrorRepository;
import com.ramesh.dataprocessing.transaction.Transaction;
import com.ramesh.dataprocessing.transaction.TransactionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class ProcessingJobController {

    private final ProcessingJobService service;
    private final ProcessingErrorRepository processingErrorRepository;
    private final TransactionRepository transactionRepository;

    public ProcessingJobController(
            ProcessingJobService service,
            ProcessingErrorRepository processingErrorRepository,
            TransactionRepository transactionRepository
    ) {
        this.service = service;
        this.processingErrorRepository = processingErrorRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/upload")
    public ProcessingJobResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }

        ProcessingJob job = service.createJob(file);
        return new ProcessingJobResponse(
                job.getId(),
                job.getFileName(),
                job.getStatus(),
                job.getTotalRecords(),
                job.getProcessedRecords(),
                job.getSuccessfulRecords(),
                job.getFailedRecords(),
                job.getCreatedAt()
        );
    }

    @GetMapping("/{id}")
    public ProcessingJobResponse getJob(@PathVariable UUID id) {
        ProcessingJob job = service.getJob(id);
        return new ProcessingJobResponse(
                job.getId(),
                job.getFileName(),
                job.getStatus(),
                job.getTotalRecords(),
                job.getProcessedRecords(),
                job.getSuccessfulRecords(),
                job.getFailedRecords(),
                job.getCreatedAt()
        );
    }

    @GetMapping
    public List<ProcessingJobResponse> getAllJobs() {
        return service.getAllJobs()
                .stream()
                .map(job -> new ProcessingJobResponse(
                        job.getId(),
                        job.getFileName(),
                        job.getStatus(),
                        job.getTotalRecords(),
                        job.getProcessedRecords(),
                        job.getSuccessfulRecords(),
                        job.getFailedRecords(),
                        job.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/{id}/errors")
    public List<ProcessingError> getErrors(@PathVariable UUID id) {
        return processingErrorRepository.findByProcessingJobId(id);
    }

    @GetMapping("/{id}/transactions")
    public Page<Transaction> getTransactions(
            @PathVariable UUID id,
            Pageable pageable) {

        return transactionRepository.findByProcessingJobId(id, pageable);
    }
}