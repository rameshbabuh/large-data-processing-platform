package com.ramesh.dataprocessing.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByProcessingJobId(UUID processingJobId,  Pageable pageable);
}
