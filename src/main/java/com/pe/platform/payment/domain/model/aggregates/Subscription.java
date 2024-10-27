package com.pe.platform.payment.domain.model.aggregates;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {


    @Column(nullable = false)
    private Integer price;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    private Long profileId;




    public Subscription() {

    }

    public Subscription(Integer price, String description, Boolean paid,Long profileId) {
        this.price = price;
        this.description = description;
        this.paid = false; //change
        this.profileId=profileId;
    }
    public void updateToPaid() {
        this.paid = true;
    }

    public Integer getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getPaid() {
        return paid;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setPaid(boolean b) {
        this.paid=b;
    }
}
