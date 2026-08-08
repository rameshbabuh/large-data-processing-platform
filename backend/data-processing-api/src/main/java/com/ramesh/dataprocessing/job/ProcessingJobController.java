package com.ramesh.dataprocessing.job;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class ProcessingJobController {

    private final ProcessingJobService service;

    public ProcessingJobController(ProcessingJobService service) {
        this.service = service;
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
}