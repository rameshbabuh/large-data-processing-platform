package com.ramesh.dataprocessing.job;

import com.ramesh.dataprocessing.processing.ProcessingError;
import com.ramesh.dataprocessing.processing.ProcessingErrorRepository;
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

    public ProcessingJobController(ProcessingJobService service, ProcessingErrorRepository processingErrorRepository) {
        this.service = service;
        this.processingErrorRepository = processingErrorRepository;
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
}