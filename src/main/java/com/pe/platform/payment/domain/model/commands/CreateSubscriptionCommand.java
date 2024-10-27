package com.pe.platform.payment.domain.model.commands;

public record CreateSubscriptionCommand(Integer price, String description, Boolean paid){

    public CreateSubscriptionCommand {
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        if (paid == null) {
            throw new IllegalArgumentException("Paid cannot be null");
        }

    }

}

