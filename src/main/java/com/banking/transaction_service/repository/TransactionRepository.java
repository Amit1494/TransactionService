package com.banking.transaction_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository  extends JpaRepository<Transaction,String> {

}
