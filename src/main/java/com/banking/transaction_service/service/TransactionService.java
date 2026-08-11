package com.banking.transaction_service.service;

import com.banking.transaction_service.dto.TransactionResponse;
import com.banking.transaction_service.dto.TransferRequest;
import com.banking.transaction_service.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private static final String TRANSACTION_INITIATED_TOPIC="transaction.initiated";

    private static final String TRANSACTION_COMPLETED_TOPIC="transaction.completed";
    private static final String TRANSACTION_REFUNDED_TOPIC="transaction.refunded";

    public  TransactionResponse getTransaction(String transactionId) {

    }
    public List<TransactionResponse> getTransactionHistory(String accountNumber){

    }

    public  TransactionResponse transfer( TransferRequest request) {
        log.info("SAGA start-transfer :{}->{} amount:{} ",request.getSenderAccountNumber(),request.getReceiverAccountNumber(),request.getAmount());

    }

    public
}
