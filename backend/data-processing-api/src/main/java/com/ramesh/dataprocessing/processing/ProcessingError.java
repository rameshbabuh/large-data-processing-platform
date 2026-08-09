package com.ramesh.dataprocessing.processing;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processing_errors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingError {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID processingJobId;

    private long rowNumber;

    @Column(length = 2000)
    private String rawData;

    private String errorMessage;

    private LocalDateTime createdAt;
}