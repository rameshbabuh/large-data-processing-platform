package com.ramesh.dataprocessing.processing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingErrorRepository
    extends JpaRepository<ProcessingError, UUID> {
}
