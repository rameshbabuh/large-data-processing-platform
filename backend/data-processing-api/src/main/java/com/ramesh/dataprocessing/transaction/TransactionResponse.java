package com.ramesh.dataprocessing.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String transactionId,
        String customerId,
        BigDecimal amount,
        String currency,
        LocalDate transactionDate
) {}