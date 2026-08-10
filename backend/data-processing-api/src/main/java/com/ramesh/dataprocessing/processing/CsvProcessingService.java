package com.ramesh.dataprocessing.processing;

import com.ramesh.dataprocessing.job.ProcessingJob;
import com.ramesh.dataprocessing.job.ProcessingJobRepository;
import com.ramesh.dataprocessing.transaction.Transaction;
import com.ramesh.dataprocessing.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CsvProcessingService {

    private final TransactionRepository transactionRepository;
    private final ProcessingErrorRepository processingErrorRepository;
    private final ProcessingJobRepository processingJobRepository;

    public CsvProcessingService(
            TransactionRepository transactionRepository,
            ProcessingErrorRepository processingErrorRepository,
            ProcessingJobRepository processingJobRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.processingErrorRepository = processingErrorRepository;
        this.processingJobRepository = processingJobRepository;
    }

/*    public long countRows(Path filePath) throws IOException {
        long count = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {

            reader.readLine(); //skipping the header

            while (reader.readLine() != null) {
                count++;
            }
        }

        return count;
    }*/

    public Transaction parseTransaction(UUID jobId, String line) {
        String[] values = line.split(",");

        return Transaction.builder()
                .transactionId(values[0])
                .customerId(values[1])
                .processingJobId(jobId)
                .amount(new BigDecimal(values[2]))
                .currency(values[3])
                .transactionDate(LocalDate.parse(values[4]))
                .build();
    }

    public ProcessingResult processFile(UUID jobId, Path filePath) throws IOException {
        List<Transaction> batch = new ArrayList<>();
        int batchSize = 500;
        long successful = 0;
        long failed = 0;
        long rowNumber = 1;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            reader.readLine(); //skip header
            String line;

            while ((line = reader.readLine()) != null) {
                rowNumber++;

                try{
                    Transaction transaction = parseTransaction(jobId, line);
                    batch.add(transaction);

                    if(batch.size() >= batchSize){
                        transactionRepository.saveAll(batch);
                        batch.clear();
                    }

                    transactionRepository.save(transaction);
                    successful++;
                } catch (Exception e) {
                    ProcessingError processingError = ProcessingError.builder()
                            .processingJobId(jobId)
                            .rowNumber(rowNumber)
                            .rawData(line)
                            .errorMessage(e.getMessage())
                            .createdAt(LocalDateTime.now())
                            .build();
                    processingErrorRepository.save(processingError);
                    failed++;
                }

                if ((successful + failed) % 100 == 0) {
                    ProcessingJob job = processingJobRepository.findById(jobId)
                            .orElseThrow();

                    job.setProcessedRecords(successful + failed);
                    job.setSuccessfulRecords(successful);
                    job.setFailedRecords(failed);

                    processingJobRepository.save(job);
                }
            }

            //remaining lines
            if (!batch.isEmpty()) {
                transactionRepository.saveAll(batch);
            }
        }
        return new ProcessingResult(successful, failed);
    }
}
