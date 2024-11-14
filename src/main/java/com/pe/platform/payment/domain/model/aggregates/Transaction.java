package com.pe.platform.payment.domain.model.aggregates;

import com.pe.platform.payment.domain.model.valueobjects.PaymentStatus;
import com.pe.platform.payment.domain.model.valueobjects.SubscriptionStatus;
import com.pe.platform.profiles.domain.model.aggregates.Profile;
import com.pe.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Transaction extends AuditableAbstractAggregateRoot {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Profile buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private Profile seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Double amount;

    public Transaction() {}

    public Transaction(Profile buyer, Profile seller, Vehicle vehicle, Double amount) {
        this.buyer = buyer;
        this.seller = seller;
        this.vehicle = vehicle;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
    }


    public void setStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }
}
