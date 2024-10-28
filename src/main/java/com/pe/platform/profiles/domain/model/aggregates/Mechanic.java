package com.pe.platform.profiles.domain.model.aggregates;

import com.pe.platform.profiles.domain.model.valueobjects.PersonName;
import com.pe.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class Mechanic extends AuditableAbstractAggregateRoot<Mechanic> {

    @Embedded
    private PersonName name;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String address;

    @Column
    private String password;

    protected Mechanic() { }

    public Mechanic(String firstName, String lastName, String email, String phoneNumber, String address, String password) {
        this.name = new PersonName(firstName, lastName);
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }


}
