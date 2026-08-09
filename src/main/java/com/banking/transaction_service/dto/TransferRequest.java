package com.banking.transaction_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    @NotBlank(message="Sender Account number is required")
    private String senderAccountNumber;

    @NotBlank(message="Receiver Account number is required")
    private String receiverAccountNumber;

    @NotBlank(message="amount number is required")
    @Positive(message="amount must be positive ")
    private BigDecimal amount;

    @NotBlank(message="Sender Account number is required")

    private String description;




}
