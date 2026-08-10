package com.ramesh.dataprocessing.job;

import com.ramesh.dataprocessing.processing.ProcessingErrorRepository;
import com.ramesh.dataprocessing.processing.ProcessingErrorResponse;
import com.ramesh.dataprocessing.transaction.TransactionRepository;
import com.ramesh.dataprocessing.transaction.TransactionResponse;
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

    @GetMapping
    public Page<ProcessingJobResponse> getAllJobs(Pageable pageable) {
        return service.getAllJobs(pageable)
                .map(job -> new ProcessingJobResponse(
                        job.getId(),
                        job.getFileName(),
                        job.getStatus(),
                        job.getTotalRecords(),
                        job.getProcessedRecords(),
                        job.getSuccessfulRecords(),
                        job.getFailedRecords(),
                        job.getCreatedAt()
                ));
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

    @GetMapping("/{id}/errors")
    public List<ProcessingErrorResponse> getErrors(@PathVariable UUID id) {
        return processingErrorRepository
                .findByProcessingJobId(id)
                .stream()
                .map(error -> new ProcessingErrorResponse(
                        error.getId(),
                        error.getRowNumber(),
                        error.getRawData(),
                        error.getErrorMessage(),
                        error.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/{id}/transactions")
    public Page<TransactionResponse> getTransactions(
            @PathVariable UUID id,
            Pageable pageable) {

        return transactionRepository.findByProcessingJobId(id, pageable)
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getTransactionId(),
                        transaction.getCustomerId(),
                        transaction.getAmount(),
                        transaction.getCurrency(),
                        transaction.getTransactionDate()
                ));
    }
}