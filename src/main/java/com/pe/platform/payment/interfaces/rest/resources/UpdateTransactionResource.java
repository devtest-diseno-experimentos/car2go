package com.pe.platform.payment.interfaces.rest.resources;

import com.pe.platform.payment.domain.model.valueobjects.PaymentStatus;

public record UpdateTransactionResource(Long transactionId, PaymentStatus status) {
}
