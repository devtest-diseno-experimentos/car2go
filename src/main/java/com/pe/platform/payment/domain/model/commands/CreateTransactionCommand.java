package com.pe.platform.payment.domain.model.commands;

public record CreateTransactionCommand(Long vehicleId) {
    public CreateTransactionCommand {
        if (vehicleId == null || vehicleId <= 0) {
            throw new IllegalArgumentException("VehicleId cannot be null");
        }
    }
}
