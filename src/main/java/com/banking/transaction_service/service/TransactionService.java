package com.banking.transaction_service.service;

import com.banking.transaction_service.client.AccountServiceClient;
import com.banking.transaction_service.dto.TransactionResponse;
import com.banking.transaction_service.dto.TransferRequest;
import com.banking.transaction_service.entity.Transaction;
import com.banking.transaction_service.entity.TransactionStatus;
import com.banking.transaction_service.entity.TransactionType;
import com.banking.transaction_service.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private static final String TRANSACTION_INITIATED_TOPIC="transaction.initiated";

    private static final String TRANSACTION_COMPLETED_TOPIC="transaction.completed";
    private static final String TRANSACTION_REFUNDED_TOPIC="transaction.refunded";


//    Saga Step 1:Initiate Transfer
//            Deducts from sender via feign
//    Saves transaction as processing
//            publish event to kafka for fraud check
//            return
//            @param request
//            @return

    public TransactionResponse transfer(TransferRequest request){
                log.info("Saga Start :Transfer :{}->{} amount:{} ",
                        request.getSenderAccountNumber(),
                        request.getReceiverAccountNumber(),
                        request.getAmount());
                accountServiceClient.deductBalance(
                        request.getSenderAccountNumber(),
                        request.getAmount()
                );
                Transaction transaction=new Transaction();
                transaction.setSenderAccountNumber(request.getSenderAccountNumber());
                transaction.setReceiverAccountNumber(request.getReceiverAccountNumber());
                transaction.setAmount(request.getAmount());
                transaction.setType(TransactionType.TRANSFER);
                transaction.setStatus(TransactionStatus.PROCESSING);
                transaction.setDescription(request.getDescription());
                transaction.setReferenceNumber(UUID.randomUUID().toString());
                Transaction savedTransaction=transactionRepository.save(transaction);
                log.info("Transaction Saved as processing :"+savedTransaction.getId());




    }

    public  TransactionResponse transfer( TransferRequest request) {
        log.info("SAGA start-transfer :{}->{} amount:{} ",request.getSenderAccountNumber(),request.getReceiverAccountNumber(),request.getAmount());

    }

    public
}
