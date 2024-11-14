package com.pe.platform.payment.domain.model.aggregates;

import com.pe.platform.payment.domain.model.valueobjects.PaymentStatus;
import com.pe.platform.payment.domain.model.valueobjects.SubscriptionStatus;
import com.pe.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Transaction extends AuditableAbstractAggregateRoot {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    private Double amount;

    public Transaction() {}

    public Transaction( Long buyerId, Long sellerId, Long vehicleId, Double amount) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.vehicleId = vehicleId;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
    }


    public void setStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }
}
