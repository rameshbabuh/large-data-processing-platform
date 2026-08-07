package com.ramesh.dataprocessing.job;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class ProcessingJobController {

    private final ProcessingJobService service;

    public ProcessingJobController(ProcessingJobService service) {
        this.service = service;
    }

    @PostMapping
    public ProcessingJob createJob(@RequestParam String fileName) {
        return service.createJob(fileName);
    }
}