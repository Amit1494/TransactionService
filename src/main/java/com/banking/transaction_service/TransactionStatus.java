package com.banking.transaction_service;

//Lifecyle flow

//        pending ,processing completed(clean Transaction )
//                           ->pending_Verification(suspicious detected)
//                                ->if user verifies then complete
//                                ->if not then flagged and block the account and saga refund
public enum TransactionStatus {

    PENDING,PROCESSING,PENDING_VERIFICATION,COMPLETED,FAILED,FLAGGED;
}
