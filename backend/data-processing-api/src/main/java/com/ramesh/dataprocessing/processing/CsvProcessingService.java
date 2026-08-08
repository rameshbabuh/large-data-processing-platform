package com.ramesh.dataprocessing.processing;

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

@Service
public class CsvProcessingService {

    private final TransactionRepository transactionRepository;
    private final ProcessingErrorRepository processingErrorRepository;

    public CsvProcessingService(
            TransactionRepository transactionRepository,
            ProcessingErrorRepository processingErrorRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.processingErrorRepository = processingErrorRepository;
    }

    public long countRows(Path filePath) throws IOException {
        long count = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {

            reader.readLine(); //skipping the header

            while (reader.readLine() != null) {
                count++;
            }
        }

        return count;
    }

    public Transaction parseTransaction(String line) {
        String[] values = line.split(",");

        return Transaction.builder()
                .transactionId(values[0])
                .customerId(values[1])
                .amount(new BigDecimal(values[2]))
                .currency(values[3])
                .transactionDate(LocalDate.parse(values[4]))
                .build();
    }

    public ProcessingResult processFile(Path filePath) throws IOException {
        long successful = 0;
        long failed = 0;
        long rowNumber = 1;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            reader.readLine(); //skip header
            String line;

            while ((line = reader.readLine()) != null) {
                rowNumber++;

                try{
                    Transaction transaction = parseTransaction(line);
                    transactionRepository.save(transaction);
                    successful++;
                } catch (Exception e) {
                    ProcessingError processingError = ProcessingError.builder()
                            .rowNumber(rowNumber)
                            .rawData(line)
                            .errorMessage(e.getMessage())
                            .createdAt(LocalDateTime.now())
                            .build();
                    processingErrorRepository.save(processingError);
                    failed++;
                }
            }
        }
        return new ProcessingResult(successful, failed);
    }
}
