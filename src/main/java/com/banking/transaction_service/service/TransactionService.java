package com.banking.transaction_service.service;

import com.banking.transaction_service.client.AccountServiceClient;
import com.banking.transaction_service.dto.TransactionResponse;
import com.banking.transaction_service.dto.TransferRequest;
import com.banking.transaction_service.entity.Transaction;
import com.banking.transaction_service.entity.TransactionStatus;
import com.banking.transaction_service.entity.TransactionType;
import com.banking.transaction_service.event.TransactionInitiatedEvent;
import com.banking.transaction_service.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private static final String TRANSACTION_INITIATED_TOPIC = "transaction.initiated";

    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction.completed";
    private static final String TRANSACTION_REFUNDED_TOPIC = "transaction.refunded";
    private final KafkaTemplate<String, Object> kafkaTemplate;

//    Saga Step 1:Initiate Transfer
//            Deducts from sender via feign
//    Saves transaction as processing
//            publish event to kafka for fraud check
//            return
//            @param request
//            @return

    public TransactionResponse transfer(TransferRequest request) {
        log.info("Saga Start :Transfer :{}->{} amount:{} ",
                request.getSenderAccountNumber(),
                request.getReceiverAccountNumber(),
                request.getAmount());
        accountServiceClient.deductBalance(
                request.getSenderAccountNumber(),
                request.getAmount()
        );
        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(request.getSenderAccountNumber());
        transaction.setReceiverAccountNumber(request.getReceiverAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PROCESSING);
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(UUID.randomUUID().toString());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction Saved as processing :" + savedTransaction.getId());

        TransactionInitiatedEvent event = new TransactionInitiatedEvent(
                savedTransaction.getId(),
                savedTransaction.getSenderAccountNumber(),
                savedTransaction.getReceiverAccountNumber(),
                savedTransaction.getAmount(),
                savedTransaction.getDescription());
        kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC,savedTransaction.getId(),event);
        log.info("Saga Step 2");
        return mapToResponse(savedTransaction);

    }
    public TransactionResponse mapToResponse(Transaction savedTransaction){
        TransactionResponse response=new TransactionResponse();
        response.setId(savedTransaction.getId());
        response.setSenderAccountNumber(savedTransaction.getSenderAccountNumber());
        response.setReceiverAccountNumber(savedTransaction.getReceiverAccountNumber());
        response.setAmount(savedTransaction.getAmount());
        response.setType(savedTransaction.getType());
        response.setStatus(savedTransaction.getStatus());
        response.setDescription(savedTransaction.getDescription());
        response.setFailureReason(savedTransaction.getFailureReason());
        response.setReferenceNumber(savedTransaction.getReferenceNumber());
        response.setCreatedAt(savedTransaction.getCreatedAt());
        response.setCompletedAt(savedTransaction.getCompletedAt());
        return response;
    }

    public  TransactionResponse getTransaction(String transactionId) {
        return mapToResponse(transactionRepository.
                findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found.")));

    }


    public  List<TransactionResponse> getTransactionHistory(String accountNumber) {

        return transactionRepository.findBySenderAccountNumberOrderByCreatedAtDesc(accountNumber).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
}