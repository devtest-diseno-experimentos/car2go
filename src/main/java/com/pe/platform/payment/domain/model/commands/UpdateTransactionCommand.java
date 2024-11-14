package com.pe.platform.payment.domain.model.commands;

public record UpdateTransactionCommand(Long transactionId, String status) {
    public UpdateTransactionCommand {
        if (transactionId == null) {
            throw new IllegalArgumentException("TransactionId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
