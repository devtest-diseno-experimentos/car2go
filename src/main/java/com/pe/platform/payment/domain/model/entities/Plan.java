package com.pe.platform.payment.domain.model.entities;

import com.pe.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Plan extends AuditableAbstractAggregateRoot<Plan> {


    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double price;

    public Plan() {
    }

    public Plan(String name, Double price) {
        this.name = name;
        this.price = price;
    }

}
