package com.pe.platform.payment.domain.model.commands;

public record CreateTransactionCommand(Long buyerId, Long sellerId, Long vehicleId, Double amount) {
    public CreateTransactionCommand {
        if (buyerId == null) {
            throw new IllegalArgumentException("BuyerId cannot be null");
        }
        if (sellerId == null) {
            throw new IllegalArgumentException("SellerId cannot be null");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
    }
}
