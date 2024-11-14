package com.pe.platform.payment.domain.model.commands;

import com.pe.platform.payment.domain.model.valueobjects.PaymentStatus;

public record UpdateTransactionCommand(Long transactionId, PaymentStatus status) {
    public UpdateTransactionCommand {
        if (transactionId == null) {
            throw new IllegalArgumentException("TransactionId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
