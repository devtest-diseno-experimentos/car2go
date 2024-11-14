package com.pe.platform.payment.interfaces.rest.resources;

import com.pe.platform.payment.domain.model.valueobjects.PaymentStatus;
import com.pe.platform.profiles.domain.model.aggregates.Profile;
import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;

import java.util.Date;

public record TransactionResource(Long transactionId, Profile buyer, Profile seller, Vehicle vehicle, Double amount, Date createdAt, Date updatedAt, PaymentStatus paymentStatus) {}
